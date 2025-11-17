package com.example.learningplatform.web;

import com.example.learningplatform.entity.Course;
import com.example.learningplatform.entity.Lesson;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Course createCourse(@RequestBody CreateCourseRequest request) {
        return courseService.createCourse(
                request.teacherId(),
                request.categoryId(),
                request.title(),
                request.description(),
                request.durationInHours(),
                request.startDate(),
                request.tagIds()
        );
    }

    @GetMapping
    public List<Course> getAll() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public Course getById(@PathVariable Long id) {
        return courseService.getCourse(id);
    }

    @PutMapping("/{id}")
    public Course update(@PathVariable Long id, @RequestBody UpdateCourseRequest request) {
        return courseService.updateCourse(id, request.title(), request.description());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }

    @PostMapping("/{courseId}/modules")
    @ResponseStatus(HttpStatus.CREATED)
    public Module addModule(@PathVariable Long courseId,
                            @RequestBody CreateModuleRequest request) {
        return courseService.addModule(courseId, request.title(), request.orderIndex());
    }

    @GetMapping("/{courseId}/modules")
    public List<Module> getModules(@PathVariable Long courseId) {
        return courseService.getModulesForCourse(courseId);
    }

    @PostMapping("/modules/{moduleId}/lessons")
    @ResponseStatus(HttpStatus.CREATED)
    public Lesson addLesson(@PathVariable Long moduleId,
                            @RequestBody CreateLessonRequest request) {
        return courseService.addLesson(
                moduleId,
                request.title(),
                request.content(),
                request.videoUrl()
        );
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public List<Lesson> getLessons(@PathVariable Long moduleId) {
        return courseService.getLessonsForModule(moduleId);
    }

    public record CreateCourseRequest(
            Long teacherId,
            Long categoryId,
            String title,
            String description,
            Integer durationInHours,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            Set<Long> tagIds
    ) {}

    public record UpdateCourseRequest(String title, String description) {}
    public record CreateModuleRequest(String title, int orderIndex) {}
    public record CreateLessonRequest(String title, String content, String videoUrl) {}
}
