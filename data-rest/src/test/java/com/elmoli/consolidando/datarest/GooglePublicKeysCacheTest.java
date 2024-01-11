/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.elmoli.consolidando.datarest;

import static com.elmoli.consolidando.datarest.config.CachingConfig.CACHE_GOOGLE_PUBLIC_KEYS;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;

@SpringBootTest
@ActiveProfiles("test")
public class GooglePublicKeysCacheTest
{

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    String jwkSetUri;

    final String expiredTokenString = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjkxNDEzY2Y0ZmEwY2I5"
            + "MmEzYzNmNWEwNTQ1MDkxMzJjNDc2NjA5MzciLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodH"
            + "RwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI1NTU5ODA4ODM4NzgtNDkwbzl"
            + "sMTF0M3A0bTMyc2c3b28zcmc5dHZ1ODFsbTYuYXBwcy5nb29nbGV1c2VyY29udGVudC5j"
            + "b20iLCJhdWQiOiI1NTU5ODA4ODM4NzgtNDkwbzlsMTF0M3A0bTMyc2c3b28zcmc5dHZ1O"
            + "DFsbTYuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTQyNjkwMTA4MTM"
            + "xNTkwOTA3NzYiLCJoZCI6ImVsbW9saWRlcG9uZW50LmNvbSIsImVtYWlsIjoibGVzZ29sZ"
            + "mVzQGVsbW9saWRlcG9uZW50LmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYmYiOjE"
            + "3MDQ4MjMxODgsIm5hbWUiOiJFbCBNb2zDrSBkZSBQb25lbnQiLCJwaWN0dXJlIjoiaHR0c"
            + "HM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EvQUNnOG9jSl81ZmVvdWtUbU5GOVl"
            + "Nb0lBdmJabzRPeFMxSmxSNGliUlpqa2Ewd1E5VjJnPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6I"
            + "kVsIE1vbMOtIiwiZmFtaWx5X25hbWUiOiJkZSBQb25lbnQiLCJsb2NhbGUiOiJlbiIsIml"
            + "hdCI6MTcwNDgyMzQ4OCwiZXhwIjoxNzA0ODI3MDg4LCJqdGkiOiI2NzRmZDUwMmVlOTFmN"
            + "jM5OTliYWE5ODNiNWMyYTUxZjgxYWNlZWJiIn0.OZwwpUJbRbjiFFnzBLmDsTiMZEZknny"
            + "Z1ZXlOKBUmYdYEr8fjQ6vcw8Lrgd7L4t_xpq-i20fwk3nZ9bAH7pBCwfH0Vk5Hs6zp8hhQ"
            + "VfdwypEXXkCJIrviWUXuXNi99tO4vR0RDExW9Zerox0JtNRKywARRYKNomOq0Pyx4g-PV-"
            + "hWeyWaBCfXXp__xqiYJxHvrf8IGdWoutxLj5iRHBTv9d144gT6_02EMfV3_jl2hxJhmxvn"
            + "GJUw3HVxAX_jITUrdeUYmQ1wydJQGo4u_Zsssd58apzYjzQNxEKRnLWIkMtVEpGzVPzzYw"
            + "6MYngiW85Y-OV7vXKbl6PHmDdcm6R-Q";

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private CacheManager cacheManager;

    @Test
    public void CacheTest()
    {
        assertNotNull(jwtDecoder);
        assertNotNull(cacheManager);

        Cache cache = cacheManager.getCache(CACHE_GOOGLE_PUBLIC_KEYS);
        assertNotNull(cache);

        cache.clear();
        
        // The token is expired but decoder is using public keys for checking signature
        // So, keys are loaded to the cache
        try
        {
            jwtDecoder.decode(expiredTokenString);    
        } catch (JwtException e)
        {
        }
        
        assertNotNull(cache.get(jwkSetUri));

    }
}
