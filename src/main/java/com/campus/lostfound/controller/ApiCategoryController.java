package com.campus.lostfound.controller;

import com.campus.lostfound.common.ApiResult;
import com.campus.lostfound.entity.Category;
import com.campus.lostfound.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class ApiCategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ApiResult<List<Category>> list() {
        return ApiResult.ok(categoryRepository.findAll());
    }
}
