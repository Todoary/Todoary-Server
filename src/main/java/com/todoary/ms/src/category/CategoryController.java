package com.todoary.ms.src.category;

import com.todoary.ms.src.category.dto.GetCategoryRes;
import com.todoary.ms.src.category.dto.PostCategoryReq;
import com.todoary.ms.src.category.model.Category;
import com.todoary.ms.src.category.dto.PostCategoryRes;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
     * [POST] /category
     *
     * @param request title color
     * @return
     */

    @PostMapping("")
    public BaseResponse<PostCategoryRes> postCategory(HttpServletRequest request, @RequestBody PostCategoryReq postCategoryReq) {
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            Long categoryId = categoryService.createCategory(user_id, postCategoryReq);
            return new BaseResponse<>(new PostCategoryRes(categoryId));
        } catch (BaseException e) {
            log.warn(e.getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 4.2 카테고리 조회 api
     *[GET] /category
     *
     * @param request
     * @return categories
     * @throws BaseException
     */
    @GetMapping("")
    public BaseResponse<GetCategoryRes> getCategory(HttpServletRequest request) {
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            List<Category> categories = new ArrayList<>(categoryProvider.retrieveById(user_id));

            if (categories.isEmpty())
                throw new BaseException(BaseResponseStatus.EMPTY_CATEGORY);

            GetCategoryRes getCategoryRes = new GetCategoryRes(categories);
            return new BaseResponse<>(getCategoryRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 4.4 카테고리 삭제 api
     * [DELETE] /category/:categoryId
     *
     * @param request categoryId
     * @return
     */
    @DeleteMapping("/{categoryId}")
    public BaseResponse<BaseResponseStatus> deleteCategory(HttpServletRequest request, @PathVariable("categoryId") long categoryId) {
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            categoryService.removeCategory(user_id, categoryId);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            log.warn(e.getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }

}
