package com.example.learningplatform.repository;

import com.example.learningplatform.entity.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {

    List<AnswerOption> findByQuestion_Id(Long questionId);
}
