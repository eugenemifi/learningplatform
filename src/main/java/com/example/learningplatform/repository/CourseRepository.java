package com.example.learningplatform.repository;

import com.example.learningplatform.entity.Course;
import com.example.learningplatform.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByCategory(Category category);

    List<Course> findByTeacherId(Long teacherId);
}
