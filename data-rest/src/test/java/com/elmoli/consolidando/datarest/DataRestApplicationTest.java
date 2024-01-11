/*
 * Copyright (c) 2023-2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */
package com.elmoli.consolidando.datarest;

import static com.elmoli.consolidando.datarest.Constants.EXPIRED_TOKEN_STRING;
import com.elmoli.consolidando.datarest.config.CachingConfig;
import static com.elmoli.consolidando.datarest.config.CachingConfig.CACHE_GOOGLE_PUBLIC_KEYS;
import com.elmoli.consolidando.datarest.domain.User;
import com.elmoli.consolidando.datarest.domain.UserId;
import com.elmoli.consolidando.datarest.domain.UserPicture;
import com.elmoli.consolidando.datarest.domain.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.test.util.AssertionErrors.assertNull;
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

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    String jwkSetUri;

    String TEST_EMAIL = "test@test.com";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private JwtDecoder jwtDecoder;

    private JwtRequestPostProcessor jwt;

    @BeforeEach
    public void setup()
    {
        jwt = SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .jwt((t) -> t.claim("email", TEST_EMAIL));
    }

    /**
     * Tests the caching of Google public keys.
     */
    @Test
    public void googlePublicKeyCacheTest()
    {
        // Step 1: Ensure that necessary components are injected
        assertNotNull(jwtDecoder);
        assertNotNull(cacheManager);

        // Step 2: Ensure that the cache manager has been injected
        Cache cache = cacheManager.getCache(CACHE_GOOGLE_PUBLIC_KEYS);
        assertNotNull(cache);

        // Step 3: Clear the cache to start with a clean state
        cache.clear();

        // Step 4: The token is expired, but the decoder is using public keys for checking the signature
        // So, keys are loaded to the cache
        try
        {
            jwtDecoder.decode(EXPIRED_TOKEN_STRING);
        } catch (JwtException e)
        {
            // This is expected as the token is intentionally expired
        }

        // Step 5: Ensure that the keys are loaded into the cache
        assertNotNull(cache.get(jwkSetUri));
    }

    @Test
    public void createUserAndGetFromCacheTest() throws Exception
    {
        // Step 1: Retrieve User ID with JWT
        MvcResult result = performGetRequest("/apis/users/id");
        UserId userId = objectMapper.readValue(result.getResponse().getContentAsString(), UserId.class);
        String pathId = "/apis/users/%s".formatted(userId.id());

        // Step 2: Delete Temporary User Picture if it exists
        performDeleteRequest(pathId + "/picture");

        // Step 3: Upload Temporary New User Picture
        MockMultipartFile image = new MockMultipartFile(
                "file-data",
                "consolidando.jpg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new ClassPathResource("consolidando_2.jpg").getInputStream());

        result = performMultipartPostRequest(pathId + "/picture", image);
        UserPicture userPicture = objectMapper.readValue(result.getResponse().getContentAsString(), UserPicture.class);

        // Step 4: Create or Update User
        User user = new User(TEST_EMAIL,
                "Test Name",
                "Test Description",
                userPicture.picture(),
                "https://test.test.com/");

        performPutRequest(pathId, user);

        // Step 5: Check if the first page is correctly cached
        checkIfFirstPageIsCorrectlyCachedAfterGetCollection();

        // Step 6: Check that the cache has been flushed
        checkThatCacheHasBeenFlushedAfterUserDeletion(pathId);
    }

    /**
     * Checks if the first page is correctly cached after making a GET request
     * to retrieve the users' collection.
     *
     * @throws Exception if an error occurs during the test.
     */
    private void checkIfFirstPageIsCorrectlyCachedAfterGetCollection() throws Exception
    {
        // Step 1: Retrieve Users Collection and cache them
        performGetRequest("/apis/users");

        // Step 2: Gets the first page from the repository
        PageRequest pageRequest = PageRequest.of(0, 20);
        Page<User> pageUsers = userRepository.findAll(pageRequest);

        // Step 3: Look for the previous object in the cache
        Cache cache = cacheManager.getCache(CachingConfig.CACHE_USER_PUBLIC_INFO);
        ValueWrapper valueWrapper = cache.get(pageRequest);
        Page<User> pageUsersCache = (Page<User>) valueWrapper.get();

        // Step 4: Check that the cache is consistent
        assertEquals(pageUsers, pageUsersCache, "The first page should be correctly cached.");
    }

    /**
     * Checks that the cache has been flushed after deleting a user.
     *
     * @param pathId the path of the user to be deleted.
     * @throws Exception if an error occurs during the test.
     */
    private void checkThatCacheHasBeenFlushedAfterUserDeletion(String pathId) throws Exception
    {
        // Step 1: Delete user => Cache is flushed
        performDeleteRequest(pathId);

        // Step 2: Check that the cache has been flushed
        Cache cache = cacheManager.getCache(CachingConfig.CACHE_USER_PUBLIC_INFO);
        ValueWrapper valueWrapper = cache.get(PageRequest.of(0, 20));
        assertNull("The cache should be empty after user deletion.", valueWrapper);
    }

    private MvcResult performGetRequest(String url) throws Exception
    {
        return mvc.perform(get(url).with(jwt))
                .andExpect(status().isOk())
                .andReturn();
    }

    private MvcResult performDeleteRequest(String url) throws Exception
    {
        return mvc.perform(delete(url).with(jwt))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    private MvcResult performMultipartPostRequest(String url, MockMultipartFile file) throws Exception
    {
        return mvc.perform(MockMvcRequestBuilders.multipart(url).file(file).with(jwt)
                .contentType(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
    }

    private MvcResult performPutRequest(String url, Object content) throws Exception
    {
        return mvc.perform(put(url).with(jwt).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

}
