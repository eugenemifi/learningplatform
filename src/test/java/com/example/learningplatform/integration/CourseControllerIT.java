package com.example.learningplatform.integration;

import com.example.learningplatform.entity.User;
import com.example.learningplatform.web.CourseController.CreateCourseRequest;
import com.example.learningplatform.web.CourseController.CreateLessonRequest;
import com.example.learningplatform.web.CourseController.CreateModuleRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CourseControllerIT extends BaseIntegrationTest {

    @Test
    void createCourseAddModuleAndLesson() throws Exception {
        User teacher = createTeacher("teacher@example.com");
        var category = createCategory("Programming");

        CreateCourseRequest courseReq = new CreateCourseRequest(
                teacher.getId(),
                category.getId(),
                "Hibernate Course",
                "Description",
                20,
                LocalDate.now(),
                Set.of()
        );

        String courseJson = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Hibernate Course"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long courseId = objectMapper.readTree(courseJson).get("id").asLong();

        CreateModuleRequest modReq = new CreateModuleRequest("Intro", 1);
        String moduleJson = mockMvc.perform(post("/api/courses/{courseId}/modules", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Intro"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long moduleId = objectMapper.readTree(moduleJson).get("id").asLong();

        CreateLessonRequest lessonReq = new CreateLessonRequest(
                "What is ORM?",
                "Content",
                null
        );

        mockMvc.perform(post("/api/courses/modules/{moduleId}/lessons", moduleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lessonReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("What is ORM?"));

        mockMvc.perform(get("/api/courses/{courseId}/modules", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Intro"));

        mockMvc.perform(get("/api/courses/modules/{moduleId}/lessons", moduleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("What is ORM?"));
    }
}
