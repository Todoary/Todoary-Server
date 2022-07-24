package com.todoary.ms.src.category;

import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryProvider {
    public final CategoryDao categoryDao;

    @Autowired
    public CategoryProvider(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public void assertUsersCategoriesValidById(long userId, List<Long> categories) throws BaseException {
        for (long categoryId : categories) {
            assertUsersCategoryValidById(userId, categoryId);
        }
    }

    public void assertUsersCategoryValidById(long userId, long categoryId) throws BaseException {
        if (!checkUsersCategoryById(userId, categoryId))
            throw new BaseException(BaseResponseStatus.USERS_CATEGORY_NOT_EXISTS);
    }

    public boolean checkUsersCategoryById(long userId, long categoryId) throws BaseException {
        try {
            return (categoryDao.selectExistsUsersCategoryById(userId, categoryId) == 1);
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

}
