package com.example.learningplatform.integration;

import com.example.learningplatform.entity.*;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.web.QuizController.CreateQuestionRequest;
import com.example.learningplatform.web.QuizController.CreateQuizRequest;
import com.example.learningplatform.web.QuizController.CreateQuestionRequest.OptionDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class QuizControllerIT extends BaseIntegrationTest {

    @Test
    void createQuizAndAddQuestion() throws Exception {
        User teacher = createTeacher("teacher@example.com");
        var category = createCategory("Programming");
        Course course = createCourse(teacher, category);
        Module module = createModule(course, "Intro", 1);

        CreateQuizRequest quizReq = new CreateQuizRequest(
                module.getId(),
                "Intro Quiz",
                15
        );

        String quizJson = mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Intro Quiz"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long quizId = objectMapper.readTree(quizJson).get("id").asLong();

        CreateQuestionRequest questionReq = new CreateQuestionRequest(
                "What is ORM?",
                Type.SINGLE_CHOICE,
                List.of(
                        new OptionDTO("Maps objects to DB tables", true),
                        new OptionDTO("Is a web framework", false)
                )
        );

        mockMvc.perform(post("/api/quizzes/{quizId}/questions", quizId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("What is ORM?"));

        mockMvc.perform(get("/api/quizzes/{id}", quizId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Intro Quiz"));
    }
}
