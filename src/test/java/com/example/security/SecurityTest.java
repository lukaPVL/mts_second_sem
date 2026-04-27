package com.example.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void profile_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void profile_WithUserRole_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/profile"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void docs_WithUserRoleOnly_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/docs"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "READ_PRIVILEGE")
    void docs_WithReadPrivilege_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/docs"))
                .andExpect(status().isOk());
    }
}