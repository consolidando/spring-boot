/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.datarest.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author joanr
 */
@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "This operation must be authenticated with a USER or ADMINISTRATOR Google ID token"
        )
public class OpenApiConfig
{

    @Bean
    public OpenAPI userDatabaseOpenAPI()
    {
        return new OpenAPI()
                .info(new Info().title("Consolidando Users REST API")
                        .description("This API allows users to register on the Consolidando website.")
                        .license(new License()
                                .name("Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License")
                                .url("https://creativecommons.org/licenses/by-nc-nd/4.0/"))
                        .contact(new Contact()
                                .name("Consolidando")
                                .url("https://diy.elmolidelanoguera.com/"))                       
                        .version("1.0"));
    }
}
