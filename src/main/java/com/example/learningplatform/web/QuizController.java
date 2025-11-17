package com.example.learningplatform.web;

import com.example.learningplatform.entity.Question;
import com.example.learningplatform.entity.Quiz;
import com.example.learningplatform.entity.Type;
import com.example.learningplatform.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Quiz create(@RequestBody CreateQuizRequest request) {
        return quizService.createQuiz(
                request.moduleId(),
                request.title(),
                request.timeLimitMinutes()
        );
    }

    @PostMapping("/{quizId}/questions")
    @ResponseStatus(HttpStatus.CREATED)
    public Question addQuestion(@PathVariable Long quizId,
                                @RequestBody CreateQuestionRequest request) {

        List<QuizService.AnswerOptionData> options = request.options().stream()
                .map(o -> new QuizService.AnswerOptionData(o.text(), o.correct()))
                .toList();

        return quizService.addQuestion(
                quizId,
                request.text(),
                request.type(),
                options
        );
    }

    @GetMapping("/{id}")
    public Quiz get(@PathVariable Long id) {
        return quizService.getQuiz(id);
    }

    public record CreateQuizRequest(Long moduleId, String title, Integer timeLimitMinutes) {}

    public record CreateQuestionRequest(
            String text,
            Type type,
            List<OptionDTO> options
    ) {
        public record OptionDTO(String text, boolean correct) {}
    }
}
