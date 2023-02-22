package com.todoary.ms.src.legacy.category;

import com.todoary.ms.src.legacy.category.dto.PostCategoryReq;
import com.todoary.ms.src.legacy.user.UserProvider;
import com.todoary.ms.src.legacy.BaseException;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.todoary.ms.src.common.response.BaseResponseStatus.*;

@Slf4j
@Service
public class JdbcCategoryService {

    private final CategoryProvider categoryProvider;
    private final CategoryDao categoryDao;
    private final UserProvider userProvider;

    @Autowired
    public JdbcCategoryService(CategoryProvider categoryProvider, CategoryDao categoryDao, UserProvider userProvider) {
        this.categoryProvider = categoryProvider;
        this.categoryDao = categoryDao;
        this.userProvider = userProvider;
    }

    @Transactional
    public Long createCategory(Long user_id, PostCategoryReq postCategoryReq) throws BaseException {
        if (userProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        if (categoryProvider.checkCategoryDuplicate(user_id,postCategoryReq.getTitle()))
            throw new BaseException(DUPLICATE_CATEGORY) ;

        String title = postCategoryReq.getTitle();
        Integer color = postCategoryReq.getColor();

        try {
            return categoryDao.insertCategory(user_id,title,color);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    @Transactional
    public void modifyCategory(Long user_id, Long categoryId, PostCategoryReq postCategoryReq) throws BaseException {
        if (userProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        if (!categoryProvider.checkUsersCategoryById(user_id, categoryId))
            throw new BaseException(BaseResponseStatus.USERS_CATEGORY_NOT_EXISTS);
        if (categoryProvider.checkCategoryEdit(user_id,categoryId,postCategoryReq.getTitle()))
            throw new BaseException(DUPLICATE_CATEGORY);

        try {
            categoryDao.updateCategory(user_id,categoryId,postCategoryReq);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional
    public void removeCategory(Long user_id, Long categoryId) throws BaseException {

        /* validation */
        if (userProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        if (!categoryProvider.checkUsersCategoryById(user_id, categoryId))
            throw new BaseException(BaseResponseStatus.USERS_CATEGORY_NOT_EXISTS);

        try {
            categoryDao.deleteCategory(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
