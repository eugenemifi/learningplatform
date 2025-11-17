package com.example.learningplatform.web;

import com.example.learningplatform.entity.Category;
import com.example.learningplatform.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category create(@RequestBody CreateCategoryRequest request) {
        return categoryService.createCategory(request.name());
    }

    @GetMapping
    public List<Category> getAll() {
        return categoryService.getAll();
    }

    public record CreateCategoryRequest(String name) {}
}
