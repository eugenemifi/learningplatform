package com.example.learningplatform.integration;

import com.example.learningplatform.entity.Assignment;
import com.example.learningplatform.entity.Course;
import com.example.learningplatform.entity.Lesson;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.web.AssignmentController.CreateAssignmentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AssignmentControllerIT extends BaseIntegrationTest {

    @Test
    void createAssignmentAndGetByLesson() throws Exception {
        User teacher = createTeacher("teacher@example.com");
        var category = createCategory("Programming");
        Course course = createCourse(teacher, category);
        Module module = createModule(course, "Intro", 1);
        Lesson lesson = createLesson(module, "Lesson 1");

        CreateAssignmentRequest req = new CreateAssignmentRequest(
                lesson.getId(),
                "HW #1",
                "Do something",
                LocalDateTime.now().plusDays(3),
                100
        );

        mockMvc.perform(post("/api/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        List<Assignment> assignments = assignmentRepository.findByLesson_Id(lesson.getId());
        assertThat(assignments).hasSize(1);
        Assignment saved = assignments.get(0);
        assertThat(saved.getTitle()).isEqualTo("HW #1");

        mockMvc.perform(get("/api/assignments/{id}", saved.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/assignments/by-lesson/{lessonId}", lesson.getId()))
                .andExpect(status().isOk());
    }
}