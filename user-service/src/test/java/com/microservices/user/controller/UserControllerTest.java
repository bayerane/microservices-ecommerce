package com.microservices.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.user.dto.UserUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour UserController
 * 
 * @author Baye Rane
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    @Test
    void testGetCurrentUserProfile() throws Exception {
        // Simuler les headers ajoutés par la Gateway
        mockMvc.perform(get("/users/profile")
                .header(USER_ID_HEADER, "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                .header(USER_EMAIL_HEADER, "admin@microservices.com")
                .header(USER_ROLE_HEADER, "ADMIN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.email").value("admin@microservices.com"));
    }

    @Test
    void testGetUserByIdAsAdmin() throws Exception {
        mockMvc.perform(get("/users/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                .header(USER_ID_HEADER, "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                .header(USER_EMAIL_HEADER, "admin@microservices.com")
                .header(USER_ROLE_HEADER, "ADMIN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void testGetUserByIdUnauthorized() throws Exception {
        // User essaie d'accéder au profil d'un autre utilisateur
        mockMvc.perform(get("/users/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                .header(USER_ID_HEADER, "b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22")
                .header(USER_EMAIL_HEADER, "user@microservices.com")
                .header(USER_ROLE_HEADER, "USER"))
            .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllUsersAsAdmin() throws Exception {
        mockMvc.perform(get("/users")
                .param("page", "0")
                .param("size", "10")
                .header(USER_ID_HEADER, "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                .header(USER_EMAIL_HEADER, "admin@microservices.com")
                .header(USER_ROLE_HEADER, "ADMIN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testGetAllUsersAsUserShouldFail() throws Exception {
        mockMvc.perform(get("/users")
                .header(USER_ID_HEADER, "b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22")
                .header(USER_EMAIL_HEADER, "user@microservices.com")
                .header(USER_ROLE_HEADER, "USER"))
            .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateUser() throws Exception {
        UserUpdateRequest request = UserUpdateRequest.builder()
            .firstName("Updated")
            .lastName("Name")
            .city("Paris")
            .build();

        mockMvc.perform(put("/users/b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(USER_ID_HEADER, "b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22")
                .header(USER_EMAIL_HEADER, "user@microservices.com")
                .header(USER_ROLE_HEADER, "USER"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.firstName").value("Updated"));
    }

    @Test
    void testSearchUsersAsAdmin() throws Exception {
        mockMvc.perform(get("/users/search")
                .param("query", "John")
                .header(USER_ID_HEADER, "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                .header(USER_EMAIL_HEADER, "admin@microservices.com")
                .header(USER_ROLE_HEADER, "ADMIN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testDeleteUserAsAdmin() throws Exception {
        mockMvc.perform(delete("/users/c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33")
                .header(USER_ID_HEADER, "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                .header(USER_EMAIL_HEADER, "admin@microservices.com")
                .header(USER_ROLE_HEADER, "ADMIN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testDeleteUserAsNonAdminShouldFail() throws Exception {
        mockMvc.perform(delete("/users/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                .header(USER_ID_HEADER, "b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22")
                .header(USER_EMAIL_HEADER, "user@microservices.com")
                .header(USER_ROLE_HEADER, "USER"))
            .andExpect(status().isForbidden());
    }
}