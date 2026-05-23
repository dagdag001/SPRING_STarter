package com.example.authservice.presentation.controller;

import com.example.authservice.presentation.dto.LoginRequest;
import com.example.authservice.presentation.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for REST API validation in AuthController.
 * 
 * Tests Requirements 14.5 and 14.6:
 * - REST endpoints validate request payload
 * - REST endpoints return HTTP 400 with error details for invalid data
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerValidationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void registerWithBlankUsername_shouldReturnBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("", "test@example.com", "password123");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/auth/register"));
    }
    
    @Test
    void registerWithInvalidEmail_shouldReturnBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("testuser", "invalid-email", "password123");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.details.email").exists());
    }
    
    @Test
    void registerWithShortPassword_shouldReturnBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "short");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.details.password").exists());
    }
    
    @Test
    void registerWithShortUsername_shouldReturnBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest("ab", "test@example.com", "password123");
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.details.username").exists());
    }
    
    @Test
    void loginWithBlankUsername_shouldReturnBadRequest() throws Exception {
        LoginRequest request = new LoginRequest("", "password123");
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.details.username").exists());
    }
    
    @Test
    void loginWithBlankPassword_shouldReturnBadRequest() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "");
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"))
                .andExpect(jsonPath("$.details.password").exists());
    }
    
    @Test
    void registerWithNullFields_shouldReturnBadRequest() throws Exception {
        String invalidJson = "{\"username\":null,\"email\":null,\"password\":null}";
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("ValidationError"));
    }
}
