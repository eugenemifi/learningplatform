package com.example.learningplatform.integration;

import com.example.learningplatform.entity.*;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.web.QuizSubmissionController.TakeQuizRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class QuizSubmissionControllerIT extends BaseIntegrationTest {

    @Test
    void takeQuizAndListResults() throws Exception {
        User teacher = createTeacher("teacher@example.com");
        User student = createStudent("student@example.com");
        var category = createCategory("Programming");
        Course course = createCourse(teacher, category);
        Module module = createModule(course, "Intro", 1);
        Quiz quiz = createQuiz(module);

        Question question = quiz.getQuestions().get(0);
        Long correctOptionId = question.getOptions().stream()
                .filter(AnswerOption::isCorrect)
                .findFirst()
                .orElseThrow()
                .getId();

        Map<Long, List<Long>> answers = Map.of(
                question.getId(), List.of(correctOptionId)
        );

        TakeQuizRequest req = new TakeQuizRequest(
                quiz.getId(),
                student.getId(),
                answers
        );

        mockMvc.perform(post("/api/quiz-submissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(1));

        mockMvc.perform(get("/api/quiz-submissions/by-student/{id}", student.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quiz.id").value(quiz.getId()));

        mockMvc.perform(get("/api/quiz-submissions/by-quiz/{id}", quiz.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].student.id").value(student.getId()));
    }
}
