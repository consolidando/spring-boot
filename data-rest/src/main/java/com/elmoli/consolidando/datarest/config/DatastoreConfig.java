/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.datarest.config;

import com.elmoli.consolidando.datarest.security.SecurityService;
import com.google.cloud.spring.data.datastore.repository.config.EnableDatastoreAuditing;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

/**
 *
 * @author joanr
 */
@Configuration
@EnableDatastoreAuditing
public class DatastoreConfig
{
    private final SecurityService securityService;

    public DatastoreConfig(SecurityService securityService)
    {
        this.securityService = securityService;
    }
    
    @Bean
    public AuditorAware<String> auditorProvider() 
    {
        return () -> Optional.ofNullable(securityService.getPrincipalEmail());
    }

}
