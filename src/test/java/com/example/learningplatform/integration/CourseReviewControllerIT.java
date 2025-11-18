package com.example.learningplatform.integration;

import com.example.learningplatform.entity.Course;
import com.example.learningplatform.entity.CourseReview;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.web.CourseReviewController.AddReviewRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CourseReviewControllerIT extends BaseIntegrationTest {

    @Test
    void addAndListReviews() throws Exception {
        User teacher = createTeacher("teacher@example.com");
        User student = createStudent("student@example.com");
        var category = createCategory("Programming");
        Course course = createCourse(teacher, category);

        AddReviewRequest req = new AddReviewRequest(
                course.getId(),
                student.getId(),
                5,
                "Great course"
        );

        mockMvc.perform(post("/api/course-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        List<CourseReview> reviews = courseReviewRepository.findByCourseId(course.getId());
        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getComment()).isEqualTo("Great course");
        assertThat(reviews.get(0).getRating()).isEqualTo(5);

        mockMvc.perform(get("/api/course-reviews/by-course/{id}", course.getId()))
                .andExpect(status().isOk());
    }
}
