package org.derleta.authorization.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@org.springframework.test.context.TestPropertySource(properties = "app.version=3.2.4-SNAPSHOT")
public class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void helloEndpoint_withoutHeader_shouldReturnHelloMessage() throws Exception {
        mockMvc.perform(get("/api/v1/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello"));
    }

    @Test
    void helloEndpoint_withHeader_shouldReturnHelloMessage() throws Exception {
        mockMvc.perform(get("/api/v1/hello").header("X-Requesting-App", "nebula_rest_api"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello"));
    }

    @Test
    void helloEndpoint_withInvalidMethod_withoutHeader_shouldBeForbiddenByAppFilter() throws Exception {
        mockMvc.perform(post("/api/v1/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void otherEndpoint_withoutHeader_shouldBeForbiddenByAppFilter() throws Exception {
        mockMvc.perform(get("/api/v1/invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void versionEndpoint_withAppVersionConfigured_shouldReturnConfiguredValue() throws Exception {
        mockMvc.perform(get("/api/v1/version"))
                .andExpect(status().isOk())
                .andExpect(content().string("3.2.4-SNAPSHOT"));
    }

}