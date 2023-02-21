package com.todoary.ms.src.legacy.category;

import com.todoary.ms.src.legacy.category.dto.GetCategoryRes;
import com.todoary.ms.src.legacy.category.dto.PostCategoryReq;
import com.todoary.ms.src.web.dto.category.CategorySaveResponse;
import com.todoary.ms.src.legacy.BaseException;
import com.todoary.ms.src.common.response.BaseResponse;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import com.todoary.ms.src.common.util.ColumnLengthInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.todoary.ms.src.common.util.ErrorLogWriter.writeExceptionWithAuthorizedRequest;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    private final JdbcCategoryService categoryService;
    private final CategoryProvider categoryProvider;

    @Autowired
    public CategoryController(JdbcCategoryService categoryService, CategoryProvider categoryProvider) {
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
    public BaseResponse<CategorySaveResponse> postCategory(HttpServletRequest request, @RequestBody
    PostCategoryReq postCategoryReq) {
        if (ColumnLengthInfo.getGraphemeLength(postCategoryReq.getTitle()) > ColumnLengthInfo.CATEGORY_TITLE_MAX_LENGTH)
            return new BaseResponse<>(BaseResponseStatus.DATA_TOO_LONG);
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            Long categoryId = categoryService.createCategory(user_id, postCategoryReq);
            return new BaseResponse<>(new CategorySaveResponse(categoryId));
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request, postCategoryReq.toString());
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 4.2 카테고리 수정 api
     * [PATCH] /category/:categoryId
     *
     * @param request
     * @return
     * @throws BaseException
     */
    @PatchMapping("/{categoryId}")
    public BaseResponse<BaseResponseStatus> patchCategory(HttpServletRequest request,
                                                          @PathVariable("categoryId") Long categoryId,
                                                          @RequestBody PostCategoryReq postCategoryReq) {
        if (ColumnLengthInfo.getGraphemeLength(postCategoryReq.getTitle()) > ColumnLengthInfo.CATEGORY_TITLE_MAX_LENGTH)
            return new BaseResponse<>(BaseResponseStatus.DATA_TOO_LONG);
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            categoryService.modifyCategory(user_id, categoryId, postCategoryReq);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request, postCategoryReq.toString());
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 4.3 카테고리 조회 api
     * [GET] /category
     *
     * @param request
     * @return List<GetCategoryRes>
     * @throws BaseException
     */
    @GetMapping("")
    public BaseResponse<List<GetCategoryRes>> getCategory(HttpServletRequest request) {
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            return new BaseResponse<>(categoryProvider.retrieveById(user_id));
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request);
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
    public BaseResponse<BaseResponseStatus> deleteCategory(HttpServletRequest request, @PathVariable("categoryId") Long categoryId) {
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            categoryService.removeCategory(user_id, categoryId);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request);
            return new BaseResponse<>(e.getStatus());
        }
    }

}
