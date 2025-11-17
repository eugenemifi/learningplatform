package com.example.learningplatform.repository;

import com.example.learningplatform.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByLesson_Id(Long lessonId);
}
