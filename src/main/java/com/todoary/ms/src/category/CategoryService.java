package com.todoary.ms.src.category;

import com.todoary.ms.src.category.dto.PostCategoryReq;
import com.todoary.ms.src.user.UserProvider;
import com.todoary.ms.util.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.todoary.ms.util.BaseResponseStatus.*;

@Slf4j
@Service
public class CategoryService {

    private final CategoryProvider categoryProvider;
    private final CategoryDao categoryDao;
    private final UserProvider userProvider;

    @Autowired
    public CategoryService(CategoryProvider categoryProvider, CategoryDao categoryDao, UserProvider userProvider) {
        this.categoryProvider = categoryProvider;
        this.categoryDao = categoryDao;
        this.userProvider = userProvider;
    }

    public void createCategory(Long user_id, PostCategoryReq postCategoryReq) throws BaseException {
        if (userProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);

        String title = postCategoryReq.getTitle();
        String color = postCategoryReq.getColor();

        try {
            categoryDao.insertCategory(user_id,title,color);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
