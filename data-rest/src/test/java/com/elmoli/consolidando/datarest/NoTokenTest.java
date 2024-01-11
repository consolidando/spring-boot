/*
 * Copyright (c) 2023-2024 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */
package com.elmoli.consolidando.datarest;

import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * @author joanr
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class NoTokenTest
{

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void setup()
    {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity()) // Applies Spring Security configuration
                .build();
    }

    /**
     * Tests API calls with no authentication token, expecting unauthorized or
     * disallowed access.
     *
     * @throws Exception if an error occurs during the test.
     */
    @Test
    public void apiCallsWithNoTokenTest() throws Exception
    {
        // Test various API calls without providing a token
        mvc.perform(get("/apis/users/id")).andExpect(status().isUnauthorized());
        mvc.perform(post("/apis/users/id")).andExpect(status().isUnauthorized());
        mvc.perform(get("/apis/users/anyid")).andExpect(status().isUnauthorized());
        mvc.perform(post("/apis/users/anyid")).andExpect(status().isMethodNotAllowed());
        mvc.perform(delete("/apis/users/anyid")).andExpect(status().isUnauthorized());
        mvc.perform(get("/apis/users/anyid/picture")).andExpect(status().isUnauthorized());
        mvc.perform(post("/apis/users/anyid/picture")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/apis/users/anyid/picture")).andExpect(status().isUnauthorized());
    }

}
