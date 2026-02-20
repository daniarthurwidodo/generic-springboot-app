package com.company.project.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @Test
    @WithMockUser
    void helloShouldReturnHelloWorld() throws Exception {
        mockMvc.perform(get("/hello"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("Hello World"));
    }

    @Test
    @WithMockUser
    void postShouldReturnMethodNotAllowed() throws Exception {
        mockMvc.perform(post("/hello"))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithMockUser
    void putShouldReturnMethodNotAllowed() throws Exception {
        mockMvc.perform(put("/hello"))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithMockUser
    void deleteShouldReturnMethodNotAllowed() throws Exception {
        mockMvc.perform(delete("/hello"))
            .andExpect(status().isMethodNotAllowed());
    }
}
