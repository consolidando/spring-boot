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

    @GetMapping("/user")
    public Principal user(Principal principal)
    {
        return principal;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http
                .authorizeHttpRequests((request)
                        -> request.requestMatchers("/user").authenticated()
                        .anyRequest().permitAll()
                )
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
