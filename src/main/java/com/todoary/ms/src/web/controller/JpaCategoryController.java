package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.category.dto.PostCategoryRes;
import com.todoary.ms.src.config.auth.LoginMember;
import com.todoary.ms.src.service.JpaCategoryService;
import com.todoary.ms.src.web.dto.CategoryRequest;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.todoary.ms.util.BaseResponseStatus.*;

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
            @RequestBody @Valid CategoryRequest request) {
        Long categoryId = categoryService.saveCategory(memberId, request);
        return new BaseResponse<>(new PostCategoryRes(categoryId));
    }

    @PatchMapping("/{categoryId}")
    public BaseResponse<BaseResponseStatus> modifyCategory(
            @LoginMember Long memberId,
            @PathVariable Long categoryId,
            @RequestBody @Valid CategoryRequest request
    ){
        categoryService.updateCategory(memberId, categoryId, request);
        return BaseResponse.from(SUCCESS);
    }
}
