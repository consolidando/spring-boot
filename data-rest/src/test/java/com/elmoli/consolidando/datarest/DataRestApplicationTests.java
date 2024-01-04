/*
 * Copyright (c) 2023 joanribalta@elmolidelanoguera.com
 * License: CC BY-NC-ND 4.0 (https://creativecommons.org/licenses/by-nc-nd/4.0/)
 * Blog Consolidando: https://diy.elmolidelanoguera.com/
 */
package com.elmoli.consolidando.datarest;

import com.elmoli.consolidando.datarest.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
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
class DataRestApplicationTests
{

    @Autowired
    private MockMvc mvc;

    @Test
    public void integrateTestCreateUser() throws Exception
    {
        String id;
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
        ObjectMapper objectMapper = new ObjectMapper();
        Map mapObject = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        id = (String) mapObject.get("id");

        // deletes user temporary files of previous connections ----------------
        String path = "/apis/users/%s".formatted(id);
        mvc.perform(delete(path));

        // upload user profile image to a temporary file -----------------------
        MockMultipartFile image = new MockMultipartFile(
                "file-data",
                "consolidando.jpg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new ClassPathResource("consolidando_2.jpg").getInputStream());

        path = "/apis/users/%s/picture".formatted(id);

        result = mvc.perform(
                MockMvcRequestBuilders.multipart(path)
                        .file(image)
                        .with(jwt)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper2 = new ObjectMapper();
        Map mapObject2 = objectMapper2.readValue(result.getResponse().getContentAsString(), Map.class);
        String mediaLink = (String) mapObject2.get("picture");

        // creates user new user adding previous temporary image ---------------
        path = "/apis/users/%s".formatted(id);
        User user = new User(TEST_EMAIL,
                "Test Name",
                "Test Description",
                mediaLink,
                "https://test.test.com/");

        // 
        result = mvc.perform(put(path)
                .with(jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

}
