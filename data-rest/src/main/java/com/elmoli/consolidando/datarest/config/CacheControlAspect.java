/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.datarest.config;

import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author joanr
 */
@Aspect
@Component
public class CacheControlAspect
{
    @Before("@annotation(cacheControl)")
    public void configureCacheControl(CacheControl cacheControl)
    {
        long maxAge = TimeUnit.SECONDS.convert(cacheControl.maxAge(), cacheControl.timeUnit());
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        if (response != null)
        {
            response.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=" + maxAge);
        }
    }
}
