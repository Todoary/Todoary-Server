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
    private final TodoProvider todoProvider;
    private final TodoDao todoDao;
    private final UserProvider userProvider;
    private final CategoryProvider categoryProvider;

    @Autowired
    public TodoService(TodoProvider todoProvider, TodoDao todoDao, UserProvider userProvider, CategoryProvider categoryProvider) {
        this.todoProvider = todoProvider;
        this.todoDao = todoDao;
        this.userProvider = userProvider;
        this.categoryProvider = categoryProvider;
    }

    @Transactional(rollbackOn = Exception.class)
    public long createTodo(long userId, PostTodoReq postTodoReq) throws BaseException {
        AssertUserValidById(userId);
        AssertUsersCategoriesValidById(userId, postTodoReq.getCategories());
        try {
            long todoId;
            if (postTodoReq.isAlarmEnabled()) {
                todoId = todoDao.insertTodo(userId, postTodoReq.getTitle(), postTodoReq.getTargetDate(), postTodoReq.isAlarmEnabled(), postTodoReq.getTargetTime());
            } else {
                todoId = todoDao.insertTodo(userId, postTodoReq.getTitle(), postTodoReq.getTargetDate());
            }
            for (long categoryId : postTodoReq.getCategories()) {
                todoDao.insertTodoCategory(todoId, categoryId);
            }
            return todoId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void removeTodo(long userId, long todoId) throws BaseException {
        AssertUserValidById(userId);
        AssertUsersTodoValidById(userId, todoId);
        try {
            todoDao.deleteTodo(todoId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    private void AssertUsersCategoriesValidById(long userId, List<Long> categories) throws BaseException {
        for (long categoryId : categories) {
            AssertUsersCategoryValidById(userId, categoryId);
        }
    }

    private void AssertUsersCategoryValidById(long userId, long categoryId) throws BaseException {
        if (!categoryProvider.checkUsersCategoryById(userId, categoryId))
            throw new BaseException(BaseResponseStatus.USERS_CATEGORY_NOT_EXISTS);
    }

    private void AssertUserValidById(long userId) throws BaseException {
        if (userProvider.checkId(userId) == 0)
            throw new BaseException(BaseResponseStatus.USERS_EMPTY_USER_ID);
    }

    private void AssertUsersTodoValidById(long userId, long todoId) throws BaseException {
        if (!todoProvider.checkUsersTodoById(userId, todoId))
            throw new BaseException(BaseResponseStatus.USERS_TODO_NOT_EXISTS);
    }
}
