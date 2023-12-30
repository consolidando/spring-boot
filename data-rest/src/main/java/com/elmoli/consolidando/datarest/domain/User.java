/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.datarest.domain;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @Email(message = "Email should be valid.")
    @NotBlank(message = "email must be defined.")
    private String email;

    @Size(min = 5, max = 25, message = "Publisher name must be between 5 and 25 characters.")
    @NotBlank(message = "Publisher name must be defined.")
    private String name;

    @Size(min = 5, max = 25, message = "Publisher description must be between 5 and 25 characters.")
    @NotBlank(message = "Publisher description must be defined.")
    private String description;

    @NotBlank(message = "Publisher picture must be defined.")
    @URLValidator(message = "Publisher picture must be a valid URL.")
    private String picture;
    
    @NotBlank(message = "Publisher web must be defined.")
    @URLValidator(message = "Publisher web must be a valid URL.")
    private String web;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;
    
    @LastModifiedBy
    private String lastModifiedBy;

    @Version
    private int version;

    public User(String email, String name, String description, String picture, String web)
    {
        this.email = email;
        this.name = name;
        this.description = description;   
        this.picture = picture;
        this.web = web;
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
