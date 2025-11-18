package com.example.learningplatform.integration;

import com.example.learningplatform.entity.Course;
import com.example.learningplatform.entity.Lesson;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.web.CourseController.CreateCourseRequest;
import com.example.learningplatform.web.CourseController.CreateLessonRequest;
import com.example.learningplatform.web.CourseController.CreateModuleRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CourseControllerIT extends BaseIntegrationTest {

    @Test
    void createCourseAddModuleAndLesson() throws Exception {
        User teacher = createTeacher("teacher@example.com");
        var category = createCategory("Programming");

        CreateCourseRequest courseReq = new CreateCourseRequest(
                teacher.getId(),
                category.getId(),
                "Hibernate Course",
                "Description",
                20,
                LocalDate.now(),
                Set.of()
        );

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseReq)))
                .andExpect(status().isCreated());

        List<Course> courses = courseRepository.findAll();
        assertThat(courses).hasSize(1);
        Course course = courses.get(0);
        assertThat(course.getTitle()).isEqualTo("Hibernate Course");

        CreateModuleRequest modReq = new CreateModuleRequest("Intro", 1);
        mockMvc.perform(post("/api/courses/{courseId}/modules", course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modReq)))
                .andExpect(status().isCreated());

        List<Module> modules = moduleRepository.findByCourseIdOrderByOrderIndex(course.getId());
        assertThat(modules).hasSize(1);
        Module module = modules.get(0);
        assertThat(module.getTitle()).isEqualTo("Intro");

        CreateLessonRequest lessonReq = new CreateLessonRequest(
                "What is ORM?",
                "Content",
                null
        );

        mockMvc.perform(post("/api/courses/modules/{moduleId}/lessons", module.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lessonReq)))
                .andExpect(status().isCreated());

        List<Lesson> lessons = lessonRepository.findByModuleId(module.getId());
        assertThat(lessons).hasSize(1);
        Lesson lesson = lessons.get(0);
        assertThat(lesson.getTitle()).isEqualTo("What is ORM?");

        // просто проверяем, что GET-ы работают
        mockMvc.perform(get("/api/courses/{courseId}/modules", course.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/courses/modules/{moduleId}/lessons", module.getId()))
                .andExpect(status().isOk());
    }
}
