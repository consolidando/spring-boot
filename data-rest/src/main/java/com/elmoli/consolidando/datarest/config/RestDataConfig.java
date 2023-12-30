/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.datarest.config;

import com.elmoli.consolidando.datarest.domain.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ExposureConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 *
 * @author joanr
 */
@Configuration
public class RestDataConfig implements RepositoryRestConfigurer
{

    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration restConfig,
            CorsRegistry cors)
    {
        ExposureConfiguration config = restConfig.getExposureConfiguration();
        config.forDomainType(User.class).withItemExposure((metadata, httpMethods)
                -> httpMethods.disable(HttpMethod.PATCH));
        config.forDomainType(User.class).withCollectionExposure((metadata, httpMethods)
                -> httpMethods.disable(HttpMethod.POST));
    }
}
