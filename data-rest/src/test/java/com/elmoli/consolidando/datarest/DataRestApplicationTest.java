/*
 * Copyright (c) 2023-2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */
package com.elmoli.consolidando.datarest;

import com.elmoli.consolidando.datarest.config.CachingConfig;
import com.elmoli.consolidando.datarest.domain.User;
import com.elmoli.consolidando.datarest.domain.UserId;
import com.elmoli.consolidando.datarest.domain.UserPicture;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DataRestApplicationTest
{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    CacheManager cacheManager;

    @Test
    public void integrateTestCreateUser() throws Exception
    {
        MvcResult result;
        String TEST_EMAIL = "test@test.com";

        SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwt
                = jwt().authorities(
                        List.of(new SimpleGrantedAuthority("ROLE_USER")))
                        .jwt((t) ->
                        {
                            t.claim("email", TEST_EMAIL);
                        });

        // Perform a GET request to retrieve user ID with a JWT
        result = mvc.perform(get("/apis/users/id")
                .with(jwt))
                .andExpect(status().isOk())
                .andReturn();

        // Parse the JSON response to extract the user ID
        UserId userId = objectMapper.readValue(result.getResponse().getContentAsString(), UserId.class);

        // deletes user temporary files of previous connections ----------------
        String pathId = "/apis/users/%s".formatted(userId.id());
        mvc.perform(delete(pathId).
                with(jwt)).
                andExpect(status().is2xxSuccessful());

        // upload user profile image to a temporary file -----------------------
        MockMultipartFile image = new MockMultipartFile(
                "file-data",
                "consolidando.jpg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new ClassPathResource("consolidando_2.jpg").getInputStream());

        result = mvc.perform(
                MockMvcRequestBuilders.multipart(pathId+"/picture")
                        .file(image)
                        .with(jwt)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        UserPicture userPicture = objectMapper.readValue(result.getResponse().getContentAsString(), UserPicture.class);

        // creates a new user adding previous temporary image ------------------
        User user = new User(TEST_EMAIL,
                "Test Name",
                "Test Description",
                userPicture.picture(),
                "https://test.test.com/");

        // 
        result = mvc.perform(put(pathId)
                .with(jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        // get users collection ------------------------------------------------
        result = mvc.perform(get("/apis/users")
                .with(jwt))
                .andExpect(status().isOk())
                .andReturn();

        // looks for the User in the cache --------------------------------------
        Cache cache = cacheManager.getCache(CachingConfig.CACHE_USER_PUBLIC_INFO);
        boolean cacheContainsId = lookForIdInTheCache(cache, userId.id());

        assert (cacheContainsId);

        // deletes user temporary files of previous connections ----------------
        mvc.perform(delete(pathId).
                with(jwt)).
                andExpect(status().is2xxSuccessful());

        // checks that the cache has been flushed -------------------------------
        cache = cacheManager.getCache(CachingConfig.CACHE_USER_PUBLIC_INFO);
        cacheContainsId = lookForIdInTheCache(cache, userId.id());

        assert (cacheContainsId == false);
    }

    private boolean lookForIdInTheCache(Cache cache, String id)
    {
        if (cache instanceof ConcurrentMapCache concurrentMapCache)
        {
            Map<Object, Object> nativeCache = concurrentMapCache.getNativeCache();

            for (Object cachedValue : nativeCache.values())
            {
                PageImpl<User> pageImpl = (PageImpl) cachedValue;
                List<User> aux = pageImpl.getContent();
                for (User user : aux)
                {
                    if (user.getId().equals(id))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
