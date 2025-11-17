package com.example.learningplatform.web;

import com.example.learningplatform.entity.Enrollment;
import com.example.learningplatform.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Enrollment enroll(@RequestBody EnrollRequest request) {
        return enrollmentService.enroll(request.courseId(), request.studentId());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unenroll(@RequestParam Long courseId,
                         @RequestParam Long studentId) {
        enrollmentService.unenroll(courseId, studentId);
    }

    @GetMapping("/by-student/{studentId}")
    public List<Enrollment> getForStudent(@PathVariable Long studentId) {
        return enrollmentService.getForStudent(studentId);
    }

    @GetMapping("/by-course/{courseId}")
    public List<Enrollment> getForCourse(@PathVariable Long courseId) {
        return enrollmentService.getForCourse(courseId);
    }

    public record EnrollRequest(Long courseId, Long studentId) {}
}
