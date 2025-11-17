package com.example.learningplatform.web;

import com.example.learningplatform.entity.Assignment;
import com.example.learningplatform.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Assignment create(@RequestBody CreateAssignmentRequest request) {
        return assignmentService.createAssignment(
                request.lessonId(),
                request.title(),
                request.description(),
                request.dueDate(),
                request.maxScore()
        );
    }

    @GetMapping("/{id}")
    public Assignment getById(@PathVariable Long id) {
        return assignmentService.getById(id);
    }

    @GetMapping("/by-lesson/{lessonId}")
    public List<Assignment> getForLesson(@PathVariable Long lessonId) {
        return assignmentService.getForLesson(lessonId);
    }

    public record CreateAssignmentRequest(
            Long lessonId,
            String title,
            String description,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dueDate,
            Integer maxScore
    ) {}
}
