package com.example.learningplatform.integration;

import com.example.learningplatform.entity.*;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.web.QuizController.CreateQuestionRequest;
import com.example.learningplatform.web.QuizController.CreateQuestionRequest.OptionDTO;
import com.example.learningplatform.web.QuizController.CreateQuizRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        mockMvc.perform(post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quizReq)))
                .andExpect(status().isCreated());

        List<Quiz> quizzes = quizRepository.findAll();
        assertThat(quizzes).hasSize(1);
        Quiz quiz = quizzes.get(0);

        CreateQuestionRequest questionReq = new CreateQuestionRequest(
                "What is ORM?",
                Type.SINGLE_CHOICE,
                List.of(
                        new OptionDTO("Maps objects to DB tables", true),
                        new OptionDTO("Is a web framework", false)
                )
        );

        mockMvc.perform(post("/api/quizzes/{quizId}/questions", quiz.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionReq)))
                .andExpect(status().isCreated());

        // Загружаем квиз вместе с вопросами
        Quiz loaded = quizRepository.findByIdWithQuestions(quiz.getId())
                .orElseThrow();
        assertThat(loaded.getQuestions()).hasSize(1);

        Question q = loaded.getQuestions().get(0);

        // А варианты ответа достаём отдельным запросом через AnswerOptionRepository
        List<AnswerOption> options = answerOptionRepository.findByQuestion_Id(q.getId());
        assertThat(options).hasSize(2);

        mockMvc.perform(get("/api/quizzes/{id}", quiz.getId()))
                .andExpect(status().isOk());
    }
}
