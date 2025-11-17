package com.example.learningplatform.integration;

import com.example.learningplatform.entity.*;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;

    @Autowired protected UserRepository userRepository;
    @Autowired protected CategoryRepository categoryRepository;
    @Autowired protected TagRepository tagRepository;
    @Autowired protected CourseRepository courseRepository;
    @Autowired protected ModuleRepository moduleRepository;
    @Autowired protected LessonRepository lessonRepository;
    @Autowired protected AssignmentRepository assignmentRepository;
    @Autowired protected SubmissionRepository submissionRepository;
    @Autowired protected QuizRepository quizRepository;
    @Autowired protected QuestionRepository questionRepository;
    @Autowired protected AnswerOptionRepository answerOptionRepository;
    @Autowired protected QuizSubmissionRepository quizSubmissionRepository;
    @Autowired protected CourseReviewRepository courseReviewRepository;
    @Autowired protected EnrollmentRepository enrollmentRepository;

    @BeforeEach
    void cleanDb() {
        courseReviewRepository.deleteAll();
        quizSubmissionRepository.deleteAll();
        submissionRepository.deleteAll();
        assignmentRepository.deleteAll();
        lessonRepository.deleteAll();
        moduleRepository.deleteAll();
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();
        tagRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        quizRepository.deleteAll();
        questionRepository.deleteAll();
        answerOptionRepository.deleteAll();
    }

    protected User createTeacher(String email) {
        User teacher = User.builder()
                .name("Teacher")
                .email(email)
                .role(User.Role.TEACHER)
                .build();
        return userRepository.save(teacher);
    }

    protected User createStudent(String email) {
        User student = User.builder()
                .name("Student")
                .email(email)
                .role(User.Role.STUDENT)
                .build();
        return userRepository.save(student);
    }

    protected Category createCategory(String name) {
        Category cat = Category.builder().name(name).build();
        return categoryRepository.save(cat);
    }

    protected Course createCourse(User teacher, Category category) {
        Course course = Course.builder()
                .teacher(teacher)
                .category(category)
                .title("Hibernate Basics")
                .description("Intro course")
                .durationInHours(20)
                .startDate(LocalDate.now())
                .build();
        return courseRepository.save(course);
    }

    protected Module createModule(Course course, String title, int order) {
        Module m = Module.builder()
                .course(course)
                .title(title)
                .orderIndex(order)
                .build();
        return moduleRepository.save(m);
    }

    protected Lesson createLesson(Module module, String title) {
        Lesson lesson = Lesson.builder()
                .module(module)
                .title(title)
                .content("Lesson content")
                .build();
        return lessonRepository.save(lesson);
    }

    protected Assignment createAssignment(Lesson lesson) {
        Assignment a = Assignment.builder()
                .lesson(lesson)
                .title("HW1")
                .description("Do something")
                .dueDate(LocalDateTime.now().plusDays(7))
                .maxScore(100)
                .build();
        return assignmentRepository.save(a);
    }

    protected Quiz createQuiz(Module module) {
        Quiz q = Quiz.builder()
                .module(module)
                .title("Quiz 1")
                .timeLimitMinutes(10)
                .build();
        q = quizRepository.save(q);

        Question question = Question.builder()
                .quiz(q)
                .text("What is ORM?")
                .type(Type.SINGLE_CHOICE)
                .build();
        question = questionRepository.save(question);

        AnswerOption opt1 = AnswerOption.builder()
                .question(question)
                .text("Maps objects to DB tables")
                .correct(true)
                .build();
        AnswerOption opt2 = AnswerOption.builder()
                .question(question)
                .text("Replaces database engine")
                .correct(false)
                .build();
        answerOptionRepository.save(opt1);
        answerOptionRepository.save(opt2);

        return q;
    }
}
