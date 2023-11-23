package com.elmoli.consolidando;

import java.security.Principal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableWebSecurity
public class GoogleSignInApplication
{
    final String RESOURCE_USERS = "/users";

    @GetMapping(RESOURCE_USERS)
    public Principal users(Principal principal)
    {
        return principal;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http
                .authorizeHttpRequests((request)
                        -> request.requestMatchers(RESOURCE_USERS).authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exception
                        -> exception.authenticationEntryPoint(
                        (request, response, authException)
                        -> response.sendRedirect("/index.html?v=2")
                ))
                .oauth2ResourceServer((oauth2ResourceServer)
                        -> oauth2ResourceServer.jwt(Customizer.withDefaults())
                )
                .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    public static void main(String[] args)
    {
        SpringApplication.run(GoogleSignInApplication.class, args);
    }
}
