package com.example.learningplatform.web;

import com.example.learningplatform.entity.CourseReview;
import com.example.learningplatform.service.CourseReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-reviews")
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseReview add(@RequestBody AddReviewRequest request) {
        return reviewService.addReview(
                request.courseId(),
                request.studentId(),
                request.rating(),
                request.comment()
        );
    }

    @GetMapping("/by-course/{courseId}")
    public List<CourseReview> getForCourse(@PathVariable Long courseId) {
        return reviewService.getForCourse(courseId);
    }

    public record AddReviewRequest(
            Long courseId,
            Long studentId,
            int rating,
            String comment
    ) {}
}
