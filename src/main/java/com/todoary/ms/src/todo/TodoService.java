package com.todoary.ms.src.todo;

import com.todoary.ms.src.category.CategoryProvider;
import com.todoary.ms.src.todo.dto.PostTodoReq;
import com.todoary.ms.src.user.UserProvider;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
public class TodoService {
    private final TodoDao todoDao;
    private final UserProvider userProvider;
    private final CategoryProvider categoryProvider;

    @Autowired
    public TodoService(TodoDao todoDao, UserProvider userProvider, CategoryProvider categoryProvider) {
        this.todoDao = todoDao;
        this.userProvider = userProvider;
        this.categoryProvider = categoryProvider;
    }

    @Transactional(rollbackOn = Exception.class)
    public void createTodo(long userId, PostTodoReq postTodoReq) throws BaseException {
        AssertUserValidById(userId);
        AssertUserCategoriesValidById(userId, postTodoReq.getCategories());
        try {
            long todoId;
            if (postTodoReq.isAlarmEnabled()) {
                todoId = todoDao.insertTodo(userId, postTodoReq.getTitle(), postTodoReq.getTargetDate(), postTodoReq.isAlarmEnabled(), postTodoReq.getTargetTime());
            } else {
                todoId = todoDao.insertTodo(userId, postTodoReq.getTitle(), postTodoReq.getTargetDate());
            }
            for (int categoryId : postTodoReq.getCategories()) {
                todoDao.insertTodoCategory(todoId, categoryId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    private void AssertUserCategoriesValidById(long userId, List<Integer> categories) throws BaseException {
        for (int categoryId : categories) {
            if (!categoryProvider.checkUsersCategoryById(userId, categoryId))
                throw new BaseException(BaseResponseStatus.USERS_CATEGORY_NOT_EXISTS);
        }
    }

    private void AssertUserValidById(long userId) throws BaseException {
        if (userProvider.checkId(userId) == 0)
            throw new BaseException(BaseResponseStatus.USERS_EMPTY_USER_ID);
    }
}
