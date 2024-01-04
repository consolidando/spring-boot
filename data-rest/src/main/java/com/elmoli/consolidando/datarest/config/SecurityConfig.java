/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */
package com.elmoli.consolidando.datarest.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 *
 * @author joanr
 */
@Configuration
@EnableWebSecurity(debug = false)  // enabling it gives an error in compìlation time in version Spring Boot 3.2.1
@EnableMethodSecurity
public class SecurityConfig
{
    @Value("${data-rest.admin.emails}")
    public String adminEmailsList;

    static public String ROLE_PREFIX = "ROLE_";
    static public String USER_ROLE = "USER";
    static public String ADMIN_ROLE = "ADMIN";
            
    private HandlerExceptionResolver exceptionResolver;

    public SecurityConfig(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver)
    {
        this.exceptionResolver = exceptionResolver;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http
                .headers(headers
                        -> headers
                        .defaultsDisabled()
                        .contentTypeOptions(Customizer.withDefaults())
                        .xssProtection(Customizer.withDefaults())
                        .httpStrictTransportSecurity(Customizer.withDefaults())
                        .frameOptions(Customizer.withDefaults()))
                .authorizeHttpRequests((request)
                        -> request
                        .requestMatchers("apis/users").permitAll()
                        .requestMatchers("apis/users/*").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exception
                        -> exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()))
                .oauth2ResourceServer((oauth2ResourceServer)
                        -> oauth2ResourceServer.jwt(Customizer.withDefaults())
                )
                .sessionManagement(sessionManagement
                        -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter customJwtAuthenticationConverter()
    {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter((Jwt source) ->
        {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            String email = source.getClaimAsString("email");
            String[] adminEmails = adminEmailsList.split(",");
            if (Arrays.asList(adminEmails).contains(email))
            {
                authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + ADMIN_ROLE));
            } else
            {
                authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + USER_ROLE));
            }
            return authorities;
        });

        return converter;
    }

    public AuthenticationEntryPoint authenticationEntryPoint()
    {
        return (HttpServletRequest request,
                HttpServletResponse response,
                AuthenticationException authException) ->
        {
            BearerTokenAuthenticationEntryPoint defaultVersion = new BearerTokenAuthenticationEntryPoint();
            defaultVersion.commence(request, response, authException);

            exceptionResolver.resolveException(request,
                    response,
                    null,
                    authException);
        };
    }

    AccessDeniedHandler accessDeniedHandler()
    {
        return (request, response, accessDeniedException) ->
        {
            BearerTokenAccessDeniedHandler defaultVersion =
			new BearerTokenAccessDeniedHandler();
            defaultVersion.handle(request, response, accessDeniedException);
            
            exceptionResolver.resolveException(request,
                    response,
                    null,
                    accessDeniedException);
        };
    }

}
