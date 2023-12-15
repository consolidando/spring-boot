/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.datarest.web;

import com.elmoli.consolidando.datarest.config.CachingConfig;
import com.elmoli.consolidando.datarest.domain.User;
import com.elmoli.consolidando.datarest.domain.UserRepository;
import com.elmoli.consolidando.datarest.security.SecurityService;
import com.elmoli.consolidando.datarest.storage.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author joanr
 */
@BasePathAwareController
@RequiredArgsConstructor
public class UserController
{

    private final String USER_TEMPORARY_PICTURE = "%s/temporary";
    private final String USER_PICTURE = "%s/logo";

    private final UserRepository userRepository;
    private final StorageService storageService;
    private final SecurityService securityService;

    private String getTemporatyPictureName(String id)
    {
        return (USER_TEMPORARY_PICTURE.formatted(id));
    }

    private String getPictureName(String id)
    {
        return (USER_PICTURE.formatted(id));
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Gets the ID of the authenticated user.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("users/id")
    public ResponseEntity<?> getUserId()
    {
        String tokenEmail = securityService.getPrincipalEmail();
        String id = User.getIdFromEmail(tokenEmail);

        return (ResponseEntity.ok().body(Collections.singletonMap("id", id)));

    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Gets the ID of the post body user email.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping("users/id")
    public ResponseEntity<?> getUserIdbyPost(@RequestBody Map map)
    {
        String bodyEmail = (String) map.get("email");
        String id = User.getIdFromEmail(bodyEmail);

        return (ResponseEntity.ok().body(Collections.singletonMap("id", id)));

    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Gets User Entity",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("users/{id}")
    @PreAuthorize("@securityService.isAuthorized(#id)")
    public ResponseEntity<?> getUser(@PathVariable String id)
            throws IOException
    {
        Optional<User> optional = userRepository.findById(id);

        if (optional.isPresent())
        {
            User user = optional.get();
            return ResponseEntity.ok().body(user);
        } else
        {
            return ResponseEntity.notFound().build();
        }
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Creates or updates User Info Google Datastore and Google Storage",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @CacheEvict(value = CachingConfig.CACHE_USER_PUBLIC_INFO, allEntries = true)
    @PutMapping("users/{id}")
    @PreAuthorize("@securityService.isAuthorized(#id)")
    public ResponseEntity<?> putUser(
            @PathVariable String id,
            @Valid @RequestBody User user)
            throws IOException
    {
        String temporalPicture = getTemporatyPictureName(id);

        // moves temporary picture to permanent picture ------------------------
        if (user.getMediaLink().contains(temporalPicture)
                && storageService.exists(temporalPicture));
        {
            String mediaLink = storageService
                    .rename(temporalPicture, getPictureName(id));
            user.setMediaLink(mediaLink);
        }

        // checks that the path id correspont to user email --------------------
        // and there is a media link
        String pathEmail = User.getEmailFromId(id);
        if ((user.getMediaLink() != null) && (pathEmail.equals(user.getEmail())))
        {
            user.setId(id);
            userRepository.save(user);
            return (ResponseEntity.status(HttpStatus.CREATED).body(user));
        }
        return (ResponseEntity.badRequest().build());
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Deletes User Info from Datastore and Google Storage",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @CacheEvict(value = CachingConfig.CACHE_USER_PUBLIC_INFO, allEntries = true)
    @DeleteMapping("users/{id}")
    @PreAuthorize("@securityService.isAuthorized(#id)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id)
            throws IOException
    {
        userRepository.deleteById(id);
        storageService.delete(getPictureName(id));
    }
        
    // -------------------------------------------------------------------------
    @Operation(
            summary = "Gets User Picture Media Link from Google Cloud Storage",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping(path = "users/{id}/picture")
    @PreAuthorize("@securityService.isAuthorized(#id)")
    public ResponseEntity<?> getUserPictureFromGoogleCloudStorage(
            @Parameter(description = "Id of the user") @PathVariable String id)
    {
        String mediaLink = storageService.getMediaLink(getPictureName(id));
        return ResponseEntity.ok().body(
                Collections.singletonMap("mediaLink", mediaLink));
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Saves a temporary picture that is used for creating or updating the User with a PUT {id}.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping(
            path = "users/{id}/picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("@securityService.isAuthorized(#id)")
    public ResponseEntity<?> saveTemporaryFileInGoogleCloudStorage(
            @Parameter(description = "Id of the user") @PathVariable String id,
            @Parameter(description = "Picture of the user") @RequestPart("file-data") MultipartFile file)
            throws IOException
    {
        String fileName;
        String mediaLink;

        fileName = getTemporatyPictureName(id);

        try (InputStream is = file.getInputStream())
        {
            mediaLink = storageService.save(fileName, is);

        } catch (IOException ex)
        {
            return (ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body(ex.getMessage()));
        }

        return (ResponseEntity.ok().body(
                Collections.singletonMap("mediaLink", mediaLink)));
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Deletes the temporary picture of the User Id.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @DeleteMapping("users/{id}/picture")
    @PreAuthorize("@securityService.isAuthorized(#id)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTemporaryFileFromGoogleClousStorage(
            @Parameter(description = "Id of the user") @PathVariable String id)
    {
        String fileName;

        fileName = getTemporatyPictureName(id);
        storageService.delete(fileName);
    }

}
