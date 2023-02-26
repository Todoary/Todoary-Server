package com.todoary.ms.src.legacy.todo;

import com.todoary.ms.src.legacy.category.LegacyCategoryProvider;
import com.todoary.ms.src.legacy.todo.dto.GetTodoByDateRes;
import com.todoary.ms.src.legacy.todo.dto.GetTodoByCategoryRes;
import com.todoary.ms.src.legacy.BaseException;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LegacyTodoProvider {

    private final LegacyTodoDao legacyTodoDao;

    private final LegacyCategoryProvider categoryProvider;

    @Autowired
    public LegacyTodoProvider(LegacyTodoDao legacyTodoDao, LegacyCategoryProvider categoryProvider) {
        this.legacyTodoDao = legacyTodoDao;
        this.categoryProvider = categoryProvider;
    }

    public boolean checkUsersTodoById(Long userId, Long todoId) throws BaseException {
        try {
            return (legacyTodoDao.selectExistsUsersTodoById(userId, todoId) == 1);
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
            return legacyTodoDao.selectTodoListByDate(userId, targetDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<GetTodoByCategoryRes> retrieveTodoListByCategory(Long userId, Long categoryId) throws BaseException {

        categoryProvider.assertUsersCategoryValidById(userId, categoryId);
        try {
            return legacyTodoDao.selectTodoListByCategory(userId, categoryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<Integer> retrieveDaysHavingTodoInMonth(Long userId, String yearAndMonth) throws BaseException {
        try {
            return legacyTodoDao.selectDaysHavingTodoInMonth(userId, yearAndMonth);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
