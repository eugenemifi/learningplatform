package com.example.learningplatform.web;

import com.example.learningplatform.entity.Tag;
import com.example.learningplatform.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tag create(@RequestBody CreateTagRequest request) {
        return tagService.createTag(request.name());
    }

    @GetMapping
    public List<Tag> getAll() {
        return tagService.getAll();
    }

    public record CreateTagRequest(String name) {}
}
