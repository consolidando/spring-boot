/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.datarest.domain;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

/**
 *
 * @author joanr
 */
@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User
{

    @Id
    private String id;

    @NotBlank(message = "email must be defined.")
    private String email;

    @NotBlank(message = "User name must be defined.")
    private String name;

    @NotBlank(message = "Family name must be defined.")
    private String familyName;

    @NotBlank(message = "MediaLink must be defined.")
    private String mediaLink;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;
    
    @LastModifiedBy
    private String lastModifiedBy;

    @Version
    private int version;

    public User(String email, String name, String familyName, String mediaLink)
    {
        this.email = email;
        this.name = name;
        this.familyName = familyName;   
        this.mediaLink = mediaLink;
    }

    static public String getIdFromEmail(String email)
    {
        return (new String(Base64.getUrlEncoder().encode(email.getBytes())));
    }

    static public String getEmailFromId(String id)
    {
        return (new String(Base64.getUrlDecoder().decode(id.getBytes())));
    }

}
