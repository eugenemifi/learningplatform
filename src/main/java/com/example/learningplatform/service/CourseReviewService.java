package com.example.learningplatform.service;

import com.example.learningplatform.entity.Course;
import com.example.learningplatform.entity.CourseReview;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.repository.CourseRepository;
import com.example.learningplatform.repository.CourseReviewRepository;
import com.example.learningplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseReviewService {

    private final CourseReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional
    public CourseReview addReview(Long courseId, Long studentId, int rating, String comment) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + studentId));

        CourseReview review = CourseReview.builder()
                .course(course)
                .student(student)
                .rating(rating)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();

        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<CourseReview> getForCourse(Long courseId) {
        return reviewRepository.findByCourseId(courseId);
    }
}
