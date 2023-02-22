package com.todoary.ms.src.legacy.todo;

import com.todoary.ms.src.legacy.category.CategoryProvider;
import com.todoary.ms.src.legacy.todo.dto.GetTodoByDateRes;
import com.todoary.ms.src.legacy.todo.dto.GetTodoByCategoryRes;
import com.todoary.ms.src.legacy.BaseException;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoProvider {

    private final TodoDao todoDao;

    private final CategoryProvider categoryProvider;

    @Autowired
    public TodoProvider(TodoDao todoDao, CategoryProvider categoryProvider) {
        this.todoDao = todoDao;
        this.categoryProvider = categoryProvider;
    }

    public boolean checkUsersTodoById(Long userId, Long todoId) throws BaseException {
        try {
            return (todoDao.selectExistsUsersTodoById(userId, todoId) == 1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void assertUsersTodoValidById(Long userId, Long todoId) throws BaseException {
        if (!checkUsersTodoById(userId, todoId))
            throw new BaseException(BaseResponseStatus.USERS_TODO_NOT_EXISTS);
    }

    public List<GetTodoByDateRes> retrieveTodoListByDate(Long userId, String targetDate) throws BaseException {
        try {
            return todoDao.selectTodoListByDate(userId, targetDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<GetTodoByCategoryRes> retrieveTodoListByCategory(Long userId, Long categoryId) throws BaseException {

        categoryProvider.assertUsersCategoryValidById(userId, categoryId);
        try {
            return todoDao.selectTodoListByCategory(userId, categoryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<Integer> retrieveDaysHavingTodoInMonth(Long userId, String yearAndMonth) throws BaseException {
        try {
            return todoDao.selectDaysHavingTodoInMonth(userId, yearAndMonth);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
