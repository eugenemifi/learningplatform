package com.example.learningplatform.integration;

import com.example.learningplatform.entity.Course;
import com.example.learningplatform.entity.Enrollment;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.web.EnrollmentController.EnrollRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EnrollmentControllerIT extends BaseIntegrationTest {

    @Test
    void enrollAndList() throws Exception {
        User teacher = createTeacher("teacher@example.com");
        User student = createStudent("student@example.com");
        var category = createCategory("Programming");
        Course course = createCourse(teacher, category);

        EnrollRequest req = new EnrollRequest(course.getId(), student.getId());

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        List<Enrollment> byStudent = enrollmentRepository.findByStudentId(student.getId());
        assertThat(byStudent).hasSize(1);
        assertThat(byStudent.get(0).getCourse().getId()).isEqualTo(course.getId());

        List<Enrollment> byCourse = enrollmentRepository.findByCourseId(course.getId());
        assertThat(byCourse).hasSize(1);
        assertThat(byCourse.get(0).getStudent().getId()).isEqualTo(student.getId());

        mockMvc.perform(get("/api/enrollments/by-student/{studentId}", student.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/enrollments/by-course/{courseId}", course.getId()))
                .andExpect(status().isOk());
    }
}
