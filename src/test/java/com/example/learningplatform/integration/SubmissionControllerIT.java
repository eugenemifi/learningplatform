package com.example.learningplatform.integration;

import com.example.learningplatform.entity.*;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.web.SubmissionController.GradeRequest;
import com.example.learningplatform.web.SubmissionController.SubmitRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        mockMvc.perform(post("/api/submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitRequest)))
                .andExpect(status().isCreated());

        List<Submission> submissions = submissionRepository.findByAssignmentId(assignment.getId());
        assertThat(submissions).hasSize(1);
        Submission sub = submissions.get(0);
        assertThat(sub.getContent()).isEqualTo("My solution");

        GradeRequest gradeReq = new GradeRequest(95, "Good job");

        mockMvc.perform(post("/api/submissions/{id}/grade", sub.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gradeReq)))
                .andExpect(status().isOk());

        Submission graded = submissionRepository.findById(sub.getId()).orElseThrow();
        assertThat(graded.getScore()).isEqualTo(95);
        assertThat(graded.getFeedback()).isEqualTo("Good job");

        mockMvc.perform(get("/api/submissions/by-assignment/{id}", assignment.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/submissions/by-student/{id}", student.getId()))
                .andExpect(status().isOk());
    }
}
