package com.example.learningplatform.service;

import com.example.learningplatform.entity.*;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final TagRepository tagRepository;

    @Transactional
    public Course createCourse(Long teacherId,
                               Long categoryId,
                               String title,
                               String description,
                               Integer durationHours,
                               LocalDate startDate,
                               Set<Long> tagIds) {

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found: " + teacherId));

        if (teacher.getRole() != User.Role.TEACHER && teacher.getRole() != User.Role.ADMIN) {
            throw new IllegalArgumentException("User is not TEACHER: " + teacherId);
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));

        Set<Tag> tags = tagIds == null || tagIds.isEmpty()
                ? Set.of()
                : Set.copyOf(tagRepository.findAllById(tagIds));

        Course course = Course.builder()
                .teacher(teacher)
                .category(category)
                .title(title)
                .description(description)
                .durationInHours(durationHours)
                .startDate(startDate)
                .tags(tags)
                .build();

        return courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public Course getCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Transactional
    public Course updateCourse(Long id, String title, String description) {
        Course course = getCourse(id);
        course.setTitle(title);
        course.setDescription(description);
        return course;
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            return;
        }
        courseRepository.deleteById(id);
    }

    @Transactional
    public Module addModule(Long courseId, String title, int orderIndex) {
        Course course = getCourse(courseId);

        Module module = Module.builder()
                .course(course)
                .title(title)
                .orderIndex(orderIndex)
                .build();

        return moduleRepository.save(module);
    }

    @Transactional
    public Lesson addLesson(Long moduleId, String title, String content, String videoUrl) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found: " + moduleId));

        Lesson lesson = Lesson.builder()
                .module(module)
                .title(title)
                .content(content)
                .videoUrl(videoUrl)
                .build();

        return lessonRepository.save(lesson);
    }

    @Transactional(readOnly = true)
    public List<Module> getModulesForCourse(Long courseId) {
        return moduleRepository.findByCourseIdOrderByOrderIndex(courseId);
    }

    @Transactional(readOnly = true)
    public List<Lesson> getLessonsForModule(Long moduleId) {
        return lessonRepository.findByModuleId(moduleId);
    }
}
