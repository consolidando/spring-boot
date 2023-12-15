/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.datarest.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author joanr
 */
@Configuration
@EnableCaching
public class CachingConfig
{
    public final static String CACHE_USER_PUBLIC_INFO = "userPublicInfo";
    
    @Bean
    public CacheManager cacheManager()
    {
        return new ConcurrentMapCacheManager();
    }
}
