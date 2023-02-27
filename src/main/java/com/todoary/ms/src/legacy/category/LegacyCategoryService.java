package com.todoary.ms.src.legacy.category;

import com.todoary.ms.src.legacy.category.dto.PostCategoryReq;
import com.todoary.ms.src.legacy.user.LegacyUserProvider;
import com.todoary.ms.src.legacy.BaseException;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.todoary.ms.src.common.response.BaseResponseStatus.*;

@Slf4j
@Service
public class LegacyCategoryService {

    private final LegacyCategoryProvider categoryProvider;
    private final LegacyCategoryDao legacyCategoryDao;
    private final LegacyUserProvider legacyUserProvider;

    @Autowired
    public LegacyCategoryService(LegacyCategoryProvider categoryProvider, LegacyCategoryDao legacyCategoryDao, LegacyUserProvider legacyUserProvider) {
        this.categoryProvider = categoryProvider;
        this.legacyCategoryDao = legacyCategoryDao;
        this.legacyUserProvider = legacyUserProvider;
    }

    @Transactional
    public Long createCategory(Long user_id, PostCategoryReq postCategoryReq) throws BaseException {
        if (legacyUserProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        if (categoryProvider.checkCategoryDuplicate(user_id,postCategoryReq.getTitle()))
            throw new BaseException(DUPLICATE_CATEGORY) ;

        String title = postCategoryReq.getTitle();
        Integer color = postCategoryReq.getColor();

        try {
            return legacyCategoryDao.insertCategory(user_id,title,color);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    @Transactional
    public void modifyCategory(Long user_id, Long categoryId, PostCategoryReq postCategoryReq) throws BaseException {
        if (legacyUserProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        if (!categoryProvider.checkUsersCategoryById(user_id, categoryId))
            throw new BaseException(BaseResponseStatus.USERS_CATEGORY_NOT_EXISTS);
        if (categoryProvider.checkCategoryEdit(user_id,categoryId,postCategoryReq.getTitle()))
            throw new BaseException(DUPLICATE_CATEGORY);

        try {
            legacyCategoryDao.updateCategory(user_id,categoryId,postCategoryReq);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional
    public void removeCategory(Long user_id, Long categoryId) throws BaseException {

        /* validation */
        if (legacyUserProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        if (!categoryProvider.checkUsersCategoryById(user_id, categoryId))
            throw new BaseException(BaseResponseStatus.USERS_CATEGORY_NOT_EXISTS);

        try {
            legacyCategoryDao.deleteCategory(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
