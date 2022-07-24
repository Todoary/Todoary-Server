package com.todoary.ms.src.category;

import com.todoary.ms.src.category.model.Category;
import com.todoary.ms.src.user.UserProvider;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.todoary.ms.util.BaseResponseStatus.DATABASE_ERROR;
import static com.todoary.ms.util.BaseResponseStatus.USERS_EMPTY_USER_ID;

@Slf4j
@Service
public class CategoryProvider {
    public final CategoryDao categoryDao;
    private final UserProvider userProvider;

    @Autowired
    public CategoryProvider(CategoryDao categoryDao, UserProvider userProvider) {
        this.categoryDao = categoryDao;
        this.userProvider = userProvider;
    }

    public boolean checkUsersCategoryById(long userId, long categoryId) throws BaseException {
        try {
            return (categoryDao.selectExistsUsersCategoryById(userId, categoryId) == 1);
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public Category retrieveById(Long user_id) throws BaseException {
        if (userProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try {
            return categoryDao.selectById(user_id);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
