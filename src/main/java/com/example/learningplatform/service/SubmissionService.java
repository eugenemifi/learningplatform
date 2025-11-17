package com.example.learningplatform.service;

import com.example.learningplatform.entity.Assignment;
import com.example.learningplatform.entity.Submission;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.repository.AssignmentRepository;
import com.example.learningplatform.repository.SubmissionRepository;
import com.example.learningplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Submission submit(Long assignmentId, Long studentId, String content) {
        if (submissionRepository.existsByStudentIdAndAssignmentId(studentId, assignmentId)) {
            throw new IllegalStateException("Submission already exists for this assignment");
        }

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        Submission submission = Submission.builder()
                .assignment(assignment)
                .student(student)
                .submittedAt(LocalDateTime.now())
                .content(content)
                .build();

        return submissionRepository.save(submission);
    }

    @Transactional
    public Submission grade(Long submissionId, Integer score, String feedback) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found: " + submissionId));

        submission.setScore(score);
        submission.setFeedback(feedback);
        return submission;
    }

    @Transactional(readOnly = true)
    public List<Submission> getForAssignment(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    @Transactional(readOnly = true)
    public List<Submission> getForStudent(Long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }
}
