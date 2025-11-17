package com.example.learningplatform.web;

import com.example.learningplatform.entity.Submission;
import com.example.learningplatform.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Submission submit(@RequestBody SubmitRequest request) {
        return submissionService.submit(
                request.assignmentId(),
                request.studentId(),
                request.content()
        );
    }

    @PostMapping("/{id}/grade")
    public Submission grade(@PathVariable Long id,
                            @RequestBody GradeRequest request) {
        return submissionService.grade(id, request.score(), request.feedback());
    }

    @GetMapping("/by-assignment/{assignmentId}")
    public List<Submission> getForAssignment(@PathVariable Long assignmentId) {
        return submissionService.getForAssignment(assignmentId);
    }

    @GetMapping("/by-student/{studentId}")
    public List<Submission> getForStudent(@PathVariable Long studentId) {
        return submissionService.getForStudent(studentId);
    }

    public record SubmitRequest(Long assignmentId, Long studentId, String content) {}
    public record GradeRequest(Integer score, String feedback) {}
}
