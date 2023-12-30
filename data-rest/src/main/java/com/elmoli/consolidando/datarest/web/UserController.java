/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */
package com.elmoli.consolidando.datarest.web;

import com.elmoli.consolidando.datarest.config.CachingConfig;
import com.elmoli.consolidando.datarest.domain.User;
import com.elmoli.consolidando.datarest.domain.UserEmail;
import com.elmoli.consolidando.datarest.domain.UserId;
import com.elmoli.consolidando.datarest.domain.UserIdException;
import com.elmoli.consolidando.datarest.domain.UserPicture;
import com.elmoli.consolidando.datarest.domain.UserNotFoundException;
import com.elmoli.consolidando.datarest.domain.UserPictureNotFoundException;
import com.elmoli.consolidando.datarest.domain.UserRepository;
import com.elmoli.consolidando.datarest.security.SecurityService;
import com.elmoli.consolidando.datarest.storage.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
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
    @Tag(name = "Get User ID")
    @Operation(
            summary = "Gets the User ID of the authenticated user.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("users/id")
    public ResponseEntity<UserId> getUserId()
    {
        String tokenEmail = securityService.getPrincipalEmail();
        String id = User.getIdFromEmail(tokenEmail);

        return (ResponseEntity.ok().body(UserId.of(id)));
    }

    // -------------------------------------------------------------------------
    @Tag(name = "Get User ID")
    @Operation(
            summary = "Retrieves the User ID associated with the email provided in the post body.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping("users/id")
    public ResponseEntity<UserId> getUserIdbyPost(@RequestBody UserEmail userEmail)
    {
        String id = User.getIdFromEmail(userEmail.email());
        return (ResponseEntity.ok().body(UserId.of(id)));
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Gets User Info.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping("users/{id}")
    @PreAuthorize("@securityService.isAuthorized(#id)")
    public ResponseEntity<?> getUser(@PathVariable String id)
    {
        Optional<User> optional = userRepository.findById(id);

        if (!optional.isPresent())
        {
            throw new UserNotFoundException(id);
        }

        User user = optional.get();
        return ResponseEntity.ok().body(user);
    }

    /**
     * Moves the user's picture to the final location if necessary.
     *
     * @param user The user whose picture needs to be checked and, if necessary,
     * moved.
     * @param id The ID of the user to construct image names.
     * @return The URL of the picture after the possible move operation.
     * @throws UserPictureNotFoundException If the picture is not found.
     */
    String moveUserPictureIfNeeded(User user, String id) throws UserPictureNotFoundException
    {
        String temporalPicture = getTemporatyPictureName(id);
        String finalPicture = getPictureName(id);

        String sourcePicture = user.getPicture();
        String targetPicture = sourcePicture.contains(temporalPicture) ? temporalPicture
                : sourcePicture.contains(finalPicture) ? finalPicture
                : sourcePicture;

        if (storageService.exists(targetPicture))
        {
            if (sourcePicture.contains(temporalPicture))
            {
                return storageService.rename(temporalPicture, finalPicture);
            } else
            {
                return user.getPicture();
            }
        } else
        {
            throw new UserPictureNotFoundException(targetPicture);
        }
    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Creates or updates User Info in the Google Cloud Datastore and in the Google Cloud Storage.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @CacheEvict(value = CachingConfig.CACHE_USER_PUBLIC_INFO, allEntries = true)
    @PutMapping("users/{id}")
    @PreAuthorize("@securityService.isAuthorized(#id)")
    @Transactional
    public ResponseEntity<?> putUser(
            @PathVariable String id,
            @Valid @RequestBody User user)
    {
        User newUser;
        HttpStatusCode status = HttpStatus.CREATED;

        // checks that the path id correspont to user email --------------------
        String pathEmail = User.getEmailFromId(id);
        if (!pathEmail.equals(user.getEmail()))
        {
            throw new UserIdException();
        }

        // Is there a user with this id ---------------------------------------.
        Optional<User> newUserOptional = userRepository.findById(id);
        newUser = newUserOptional.orElseGet(() ->
        {
            User aux = new User();
            aux.setCreatedDate(
                    ZonedDateTime.now(ZoneId.of("Europe/Madrid")).toInstant());
            return aux;
        });

        if (newUserOptional.isPresent())
        {
            status = HttpStatus.ACCEPTED;
            newUser.setVersion(newUser.getVersion() + 1);
        }

        // moves temporary picture to permanent picture ------------------------
        newUser.setPicture(moveUserPictureIfNeeded(user, id));
        //
        newUser.setId(id);
        newUser.setEmail(user.getEmail());
        newUser.setName(user.getName());
        newUser.setDescription(user.getDescription());
        newUser.setWeb(user.getWeb());

        userRepository.save(newUser);
        return (ResponseEntity.status(status).body(newUser));

    }

    // -------------------------------------------------------------------------
    @Operation(
            summary = "Deletes User Info from the Google Cloud Datastore and its picture from the Google Cloud Storage.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @CacheEvict(value = CachingConfig.CACHE_USER_PUBLIC_INFO, allEntries = true)
    @DeleteMapping("users/{id}")
    @PreAuthorize("@securityService.isAuthorized(#id)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id)
    {
        userRepository.deleteById(id);
        storageService.delete(getPictureName(id));
    }

    // -------------------------------------------------------------------------
    @Tag(name = "User Picture in the Google Cloud Storage")
    @Operation(
            summary = "Gets Picture Media Link of the User Id from the Google Cloud Storage.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @GetMapping(path = "users/{id}/picture")
    @PreAuthorize("@securityService.isAuthorized(#id)")
    public ResponseEntity<UserPicture> getUserPictureFromGoogleCloudStorage(
            @Parameter(description = "Id of the user") @PathVariable String id)
    {
        String mediaLink = storageService.getMediaLink(getPictureName(id));
        return ResponseEntity.ok().body(UserPicture.of(mediaLink));
    }

    // -------------------------------------------------------------------------
    @Tag(name = "User Picture in the Google Cloud Storage")
    @Operation(
            summary = "Saves a temporary picture that is used for creating or updating the User with a PUT {id}.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(
            {
                @ApiResponse(responseCode = "200", description = "Successful operation"),
                @ApiResponse(responseCode = "500", description = "Google Cloud Storage Problem")
            })
    @PostMapping(
            path = "users/{id}/picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("@securityService.isAuthorized(#id)")
    public ResponseEntity<?> saveTemporaryFileInGoogleCloudStorage(
            @Parameter(description = "Id of the user") @PathVariable String id,
            @Parameter(description = "Temporaty picture of the user") @RequestPart("file-data") MultipartFile file)
            throws IOException
    {
        String fileName;
        String mediaLink;

        fileName = getTemporatyPictureName(id);

        try (InputStream is = file.getInputStream())
        {
            mediaLink = storageService.save(fileName, is);
        }

        return (ResponseEntity.ok().body(UserPicture.of(mediaLink)));
    }

    // -------------------------------------------------------------------------
    @Tag(name = "User Picture in the Google Cloud Storage")
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
