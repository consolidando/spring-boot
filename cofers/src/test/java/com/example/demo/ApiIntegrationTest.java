package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetEndpoint() throws Exception 
    {        
        mockMvc.perform(MockMvcRequestBuilders.get("/apis/characters"))
                .andExpect(MockMvcResultMatchers.status().isOk());        
    }
    
    @Test
    void testNotImplemented() throws Exception 
    {                
       // POST
        mockMvc.perform(MockMvcRequestBuilders.post("/apis/characters"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));

        // PUT
        mockMvc.perform(MockMvcRequestBuilders.put("/apis/characters/1"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));

        // DELETE
        mockMvc.perform(MockMvcRequestBuilders.delete("/apis/characters/1"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.METHOD_NOT_ALLOWED.value()));
        
    }
}

