/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */

package com.elmoli.consolidando.datarest.security;

import static com.elmoli.consolidando.datarest.config.SecurityConfig.ADMIN_ROLE;
import static com.elmoli.consolidando.datarest.config.SecurityConfig.ROLE_PREFIX;
import com.elmoli.consolidando.datarest.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 *
 * @author joanr
 */
@Service
public class SecurityService
{
    public String getPrincipalEmail()
    {
        String tokenEmail = null;
                
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated())
        {
              Object principal = authentication.getPrincipal();

                if (principal instanceof Jwt jwt)
                {
                    tokenEmail = jwt.getClaim("email");                    
                }
        }
        return(tokenEmail);        
    }        

    public boolean isAuthorized(String id)
    {
        boolean authorized = false;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated())
        {
            if (authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(ROLE_PREFIX + ADMIN_ROLE)))
            {
                authorized = true;
            } else
            {
                Object principal = authentication.getPrincipal();

                if (principal instanceof Jwt jwt)
                {
                    String tokenEmail = jwt.getClaim("email");
                    
                    if (tokenEmail.equals(User.getEmailFromId(id)))
                    {
                        authorized = true;
                    }
                }
            }
        }

        return authorized;
    }

}
