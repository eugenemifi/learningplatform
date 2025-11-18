package com.example.learningplatform.repository;

import com.example.learningplatform.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @Query("""
           select distinct q from Quiz q
           left join fetch q.questions qu
           where q.id = :id
           """)
    Optional<Quiz> findByIdWithQuestions(@Param("id") Long id);
}
