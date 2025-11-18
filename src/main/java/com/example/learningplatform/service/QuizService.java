package com.example.learningplatform.service;

import com.example.learningplatform.entity.*;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.repository.ModuleRepository;
import com.example.learningplatform.repository.QuizRepository;
import com.example.learningplatform.repository.QuestionRepository;
import com.example.learningplatform.repository.AnswerOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;

    @Transactional
    public Quiz createQuiz(Long moduleId, String title, Integer timeLimitMinutes) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found: " + moduleId));

        Quiz quiz = Quiz.builder()
                .module(module)
                .title(title)
                .timeLimitMinutes(timeLimitMinutes)
                .build();

        return quizRepository.save(quiz);
    }

    @Transactional
    public Question addQuestion(Long quizId,
                                Type type,
                                String text,
                                List<AnswerOptionData> options) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + quizId));

        Question question = Question.builder()
                .quiz(quiz)
                .text(text)
                .type(type)
                .build();

        Question savedQuestion = questionRepository.save(question);

        for (AnswerOptionData opt : options) {
            AnswerOption answerOption = AnswerOption.builder()
                    .question(savedQuestion)
                    .text(opt.text())
                    .correct(opt.correct())
                    .build();
            answerOptionRepository.save(answerOption);
        }

        return savedQuestion;
    }

    @Transactional(readOnly = true)
    public Quiz getQuiz(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + id));
    }

    public record AnswerOptionData(String text, boolean correct) {}
}
