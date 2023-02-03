package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.category.dto.PostCategoryRes;
import com.todoary.ms.src.config.auth.LoginMember;
import com.todoary.ms.src.service.JpaCategoryService;
import com.todoary.ms.src.web.dto.CategorySaveRequest;
import com.todoary.ms.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/category")
public class JpaCategoryController {

    private final JpaCategoryService categoryService;

    // 4.1 카테고리 생성 API
    @PostMapping("")
    public BaseResponse<PostCategoryRes> createCategory(
            @LoginMember Long memberId,
            @RequestBody @Valid CategorySaveRequest request) {
        Long categoryId = categoryService.saveCategory(memberId, request);
        return new BaseResponse<>(new PostCategoryRes(categoryId));
    }
}
