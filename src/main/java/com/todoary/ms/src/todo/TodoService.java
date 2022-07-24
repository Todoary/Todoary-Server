package com.todoary.ms.src.todo;

import com.todoary.ms.src.category.CategoryProvider;
import com.todoary.ms.src.todo.dto.PostTodoReq;
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
    private final CategoryProvider categoryProvider;

    @Autowired
    public TodoService(TodoProvider todoProvider, TodoDao todoDao, CategoryProvider categoryProvider) {
        this.todoProvider = todoProvider;
        this.todoDao = todoDao;
        this.categoryProvider = categoryProvider;
    }

    @Transactional(rollbackOn = Exception.class)
    public long createTodo(long userId, PostTodoReq postTodoReq) throws BaseException {
        assertUsersCategoriesValidById(userId, postTodoReq.getCategories());
        try {
            long todoId;
            if (postTodoReq.isAlarmEnabled()) {
                todoId = todoDao.insertTodo(userId, postTodoReq.getTitle(), postTodoReq.getTargetDate(), postTodoReq.isAlarmEnabled(), postTodoReq.getTargetTime());
            } else {
                todoId = todoDao.insertTodo(userId, postTodoReq.getTitle(), postTodoReq.getTargetDate());
            }
            todoDao.insertTodoCategories(todoId, postTodoReq.getCategories());
            return todoId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void removeTodo(long userId, long todoId) throws BaseException {
        todoProvider.assertUsersTodoValidById(userId, todoId);
        try {
            todoDao.deleteTodo(todoId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    private void assertUsersCategoriesValidById(long userId, List<Long> categories) throws BaseException {
        for (long categoryId : categories) {
            assertUsersCategoryValidById(userId, categoryId);
        }
    }

    private void assertUsersCategoryValidById(long userId, long categoryId) throws BaseException {
        if (!categoryProvider.checkUsersCategoryById(userId, categoryId))
            throw new BaseException(BaseResponseStatus.USERS_CATEGORY_NOT_EXISTS);
    }
}
