package com.example.demo.config;

import com.example.demo.domain.CharacterInfo;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ExposureConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * Configuration class for customizing Spring Data REST settings. Implements
 * {@link RepositoryRestConfigurer} to provide custom configuration.
 * Specifically, it disables all operations except GET for collections for the
 * domain type {@link CharacterInfo}.
 */
@Configuration
public class DataRestConfig implements RepositoryRestConfigurer
{

    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration restConfig,
            CorsRegistry cors)
    {
        ExposureConfiguration config = restConfig.getExposureConfiguration();

        // Disable all operations for items
        config.forDomainType(CharacterInfo.class).withItemExposure(((metadata, httpMethods)
                -> httpMethods.disable(HttpMethod.values())));

        // Enable only GET for collections of type CharacterInfo
        config.forDomainType(CharacterInfo.class).withCollectionExposure((metadata, httpMethods)
                -> httpMethods.enable(HttpMethod.GET));
    }
}
