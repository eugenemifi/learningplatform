package com.example.learningplatform.integration;

import com.example.learningplatform.entity.User;
import com.example.learningplatform.web.UserController.CreateUserRequest;
import com.example.learningplatform.web.UserController.UpdateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerIT extends BaseIntegrationTest {

    @Test
    void createAndGetUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                "John Doe",
                "john@example.com",
                User.Role.STUDENT
        );

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        User created = objectMapper.readValue(response, User.class);

        mockMvc.perform(get("/api/users/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));

        UpdateUserRequest upd = new UpdateUserRequest("John Updated");
        mockMvc.perform(put("/api/users/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"));

        assertThat(userRepository.findById(created.getId()).orElseThrow().getName())
                .isEqualTo("John Updated");
    }
}
