package com.todoary.ms.src.legacy.category;

import com.todoary.ms.src.legacy.category.dto.GetCategoryRes;
import com.todoary.ms.src.legacy.user.LegacyUserProvider;
import com.todoary.ms.src.legacy.BaseException;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LegacyCategoryProvider {
    public final LegacyCategoryDao legacyCategoryDao;
    private final LegacyUserProvider legacyUserProvider;

    @Autowired
    public LegacyCategoryProvider(LegacyCategoryDao legacyCategoryDao, LegacyUserProvider legacyUserProvider) {
        this.legacyCategoryDao = legacyCategoryDao;
        this.legacyUserProvider = legacyUserProvider;
    }

    public void assertUsersCategoryValidById(Long userId, Long categoryId) throws BaseException {
        if (!checkUsersCategoryById(userId, categoryId))
            throw new BaseException(BaseResponseStatus.USERS_CATEGORY_NOT_EXISTS);
    }

    public boolean checkUsersCategoryById(Long user_id, Long categoryId) throws BaseException {
        try {
            return (legacyCategoryDao.selectExistsUsersCategoryById(user_id, categoryId) == 1);
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public boolean checkCategoryDuplicate(Long user_id, String title) throws BaseException {
        try {
            return (legacyCategoryDao.selectExistsCategoryTitle(user_id, title) == 1);
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public boolean checkCategoryEdit(Long user_id,Long categoryId, String title) throws BaseException {
        try {
            return (legacyCategoryDao.selectExistsCategoryEdit(user_id, categoryId, title) == 1);
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<GetCategoryRes> retrieveById(Long user_id) throws BaseException {
        if (legacyUserProvider.checkId(user_id) == 0) throw new BaseException(BaseResponseStatus.USERS_EMPTY_USER_ID);
        try {
            return legacyCategoryDao.selectById(user_id);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

}
