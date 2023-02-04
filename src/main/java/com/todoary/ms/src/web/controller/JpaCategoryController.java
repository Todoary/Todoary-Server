package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.category.dto.PostCategoryRes;
import com.todoary.ms.src.config.auth.LoginMember;
import com.todoary.ms.src.service.JpaCategoryService;
import com.todoary.ms.src.web.dto.CategoryRequest;
import com.todoary.ms.src.web.dto.CategoryResponse;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.todoary.ms.util.BaseResponseStatus.SUCCESS;

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

    // 4.2 카테고리 수정 API
    @PatchMapping("/{categoryId}")
    public BaseResponse<BaseResponseStatus> modifyCategory(
            @LoginMember Long memberId,
            @PathVariable Long categoryId,
            @RequestBody @Valid CategoryRequest request
    ){
        categoryService.updateCategory(memberId, categoryId, request);
        return BaseResponse.from(SUCCESS);
    }

    // 4.3 카테고리 조회 API
    @GetMapping("")
    public BaseResponse<CategoryResponse[]> retrieveCategory(
            @LoginMember Long memberId){
        return new BaseResponse<>(categoryService.findCategories(memberId));
    }

    // 4.4 카테고리 삭제 API
    @DeleteMapping("/{categoryId}")
    public BaseResponse<BaseResponseStatus> deleteCategory(
            @LoginMember Long memberId,
            @PathVariable Long categoryId
    ){
        categoryService.deleteCategory(memberId, categoryId);
        return BaseResponse.from(SUCCESS);
    }
}
