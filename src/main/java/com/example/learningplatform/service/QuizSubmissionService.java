package com.example.learningplatform.service;

import com.example.learningplatform.entity.*;
import com.example.learningplatform.repository.QuizRepository;
import com.example.learningplatform.repository.QuizSubmissionRepository;
import com.example.learningplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QuizSubmissionService {

    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final UserRepository userRepository;

    /**
     * answers: Map<questionId, List<optionId>>
     */
    @Transactional
    public QuizSubmission takeQuiz(Long quizId,
                                   Long studentId,
                                   Map<Long, List<Long>> answers) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + quizId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        if (quizSubmissionRepository.existsByStudentIdAndQuizId(studentId, quizId)) {
            throw new IllegalStateException("Quiz already taken by student");
        }

        int score = calculateScore(quiz, answers);

        QuizSubmission submission = QuizSubmission.builder()
                .quiz(quiz)
                .student(student)
                .score(score)
                .takenAt(LocalDateTime.now())
                .build();

        return quizSubmissionRepository.save(submission);
    }

    private int calculateScore(Quiz quiz, Map<Long, List<Long>> answers) {
        int score = 0;
        for (Question question : quiz.getQuestions()) {
            List<Long> selected = answers.getOrDefault(question.getId(), List.of());
            if (selected.isEmpty()) {
                continue;
            }

            if (question.getType() == Type.SINGLE_CHOICE) {
                Long selectedId = selected.get(0);
                boolean correct = question.getOptions().stream()
                        .anyMatch(o -> o.getId().equals(selectedId) && o.isCorrect());
                if (correct) {
                    score++;
                }
            } else {
                Set<Long> selectedSet = new HashSet<>(selected);
                Set<Long> correctIds = new HashSet<>();
                Set<Long> allIds = new HashSet<>();
                question.getOptions().forEach(o -> {
                    allIds.add(o.getId());
                    if (o.isCorrect()) correctIds.add(o.getId());
                });

                if (selectedSet.equals(correctIds)) {
                    score++;
                }
            }
        }
        return score;
    }

    @Transactional(readOnly = true)
    public List<QuizSubmission> getForStudent(Long studentId) {
        return quizSubmissionRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<QuizSubmission> getForQuiz(Long quizId) {
        return quizSubmissionRepository.findByQuizId(quizId);
    }
}
