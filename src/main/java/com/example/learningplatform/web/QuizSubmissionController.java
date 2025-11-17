package com.example.learningplatform.web;

import com.example.learningplatform.entity.QuizSubmission;
import com.example.learningplatform.service.QuizSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz-submissions")
@RequiredArgsConstructor
public class QuizSubmissionController {

    private final QuizSubmissionService quizSubmissionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuizSubmission takeQuiz(@RequestBody TakeQuizRequest request) {
        return quizSubmissionService.takeQuiz(
                request.quizId(),
                request.studentId(),
                request.answers()
        );
    }

    @GetMapping("/by-student/{studentId}")
    public List<QuizSubmission> getForStudent(@PathVariable Long studentId) {
        return quizSubmissionService.getForStudent(studentId);
    }

    @GetMapping("/by-quiz/{quizId}")
    public List<QuizSubmission> getForQuiz(@PathVariable Long quizId) {
        return quizSubmissionService.getForQuiz(quizId);
    }

    /**
     * answers: { "questionId": [optionId1, optionId2...] }
     */
    public record TakeQuizRequest(
            Long quizId,
            Long studentId,
            Map<Long, List<Long>> answers
    ) {}
}
