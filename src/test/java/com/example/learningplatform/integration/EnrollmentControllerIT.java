package com.example.learningplatform.integration;

import com.example.learningplatform.entity.Course;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.web.EnrollmentController.EnrollRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EnrollmentControllerIT extends BaseIntegrationTest {

    @Test
    void enrollAndList() throws Exception {
        User teacher = createTeacher("teacher@example.com");
        User student = createStudent("student@example.com");
        var category = createCategory("Programming");
        Course course = createCourse(teacher, category);

        EnrollRequest req = new EnrollRequest(course.getId(), student.getId());

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        mockMvc.perform(get("/api/enrollments/by-student/{studentId}", student.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].course.id").value(course.getId()));

        mockMvc.perform(get("/api/enrollments/by-course/{courseId}", course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].student.id").value(student.getId()));
    }
}
