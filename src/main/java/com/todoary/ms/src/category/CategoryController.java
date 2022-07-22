package com.todoary.ms.src.category;

import com.todoary.ms.src.category.dto.PostCategoryReq;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryProvider categoryProvider;

    @Autowired
    public CategoryController(CategoryService categoryService, CategoryProvider categoryProvider) {
        this.categoryService = categoryService;
        this.categoryProvider = categoryProvider;
    }

    /**
     * 4.1 카테고리 생성 API
     *
     * @param request title color
     * @return
     */

    @PostMapping("")
    public BaseResponse<BaseResponseStatus> postCategory(HttpServletRequest request, @RequestBody PostCategoryReq postCategoryReq) {
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            categoryService.createCategory(user_id,postCategoryReq);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

}
