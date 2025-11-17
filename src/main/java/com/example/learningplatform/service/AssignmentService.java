package com.example.learningplatform.service;

import com.example.learningplatform.entity.Assignment;
import com.example.learningplatform.entity.Lesson;
import com.example.learningplatform.repository.AssignmentRepository;
import com.example.learningplatform.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final LessonRepository lessonRepository;

    @Transactional
    public Assignment createAssignment(Long lessonId,
                                       String title,
                                       String description,
                                       LocalDateTime dueDate,
                                       Integer maxScore) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + lessonId));

        Assignment assignment = Assignment.builder()
                .lesson(lesson)
                .title(title)
                .description(description)
                .dueDate(dueDate)
                .maxScore(maxScore)
                .build();

        return assignmentRepository.save(assignment);
    }

    @Transactional(readOnly = true)
    public Assignment getById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Assignment> getForLesson(Long lessonId) {
        return assignmentRepository.findByLesson_Id(lessonId);
    }
}
