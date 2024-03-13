/*
 * Copyright (c) 2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */
package com.elmoli.consolidando.vt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.embedded.TomcatVirtualThreadsWebServerFactoryCustomizer;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.boot.task.SimpleAsyncTaskExecutorBuilder;

/**
 *
 * @author joanr
 */
@Configuration
@Profile(
        {
            "servlet"
        })
public class TomcatConfig
{
    @Value("${app.tomcat.virtual-threads.concurrency-limit:200}")
    private int concurrenyLimit;
    
    @Bean
    @ConditionalOnProperty(name = "spring.threads.virtual.enabled", havingValue = "true")
    public TomcatVirtualThreadsWebServerFactoryCustomizer tomcatVirtualThreadsWebServerFactoryCustomizer()
    {

        final SimpleAsyncTaskExecutor simple = 
                new SimpleAsyncTaskExecutorBuilder()
                .threadNamePrefix("vt-limited")
                .virtualThreads(Boolean.TRUE)
                .concurrencyLimit(concurrenyLimit).build();
        

        return new TomcatVirtualThreadsWebServerFactoryCustomizer()
        {
            @Override
            public void customize(ConfigurableTomcatWebServerFactory factory)
            {
                if (concurrenyLimit != -1)
                {    
                factory.addProtocolHandlerCustomizers(
                        protocolHandler -> protocolHandler.setExecutor(simple));
                }
            }

            @Override
            public int getOrder()
            {
                return Ordered.LOWEST_PRECEDENCE;
            }
        };
    }
}
