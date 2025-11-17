package com.example.learningplatform.integration;

import com.example.learningplatform.entity.*;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.web.SubmissionController.GradeRequest;
import com.example.learningplatform.web.SubmissionController.SubmitRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SubmissionControllerIT extends BaseIntegrationTest {

    @Test
    void submitAndGradeAssignment() throws Exception {
        User teacher = createTeacher("teacher@example.com");
        User student = createStudent("student@example.com");
        var category = createCategory("Programming");
        Course course = createCourse(teacher, category);
        Module module = createModule(course, "Intro", 1);
        Lesson lesson = createLesson(module, "Lesson 1");
        Assignment assignment = createAssignment(lesson);

        SubmitRequest submitRequest = new SubmitRequest(
                assignment.getId(),
                student.getId(),
                "My solution"
        );

        String submissionJson = mockMvc.perform(post("/api/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("My solution"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long submissionId = objectMapper.readTree(submissionJson).get("id").asLong();

        GradeRequest gradeReq = new GradeRequest(95, "Good job");

        mockMvc.perform(post("/api/submissions/{id}/grade", submissionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gradeReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(95))
                .andExpect(jsonPath("$.feedback").value("Good job"));

        mockMvc.perform(get("/api/submissions/by-assignment/{id}", assignment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].score").value(95));

        mockMvc.perform(get("/api/submissions/by-student/{id}", student.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assignment.id").value(assignment.getId()));
    }
}
