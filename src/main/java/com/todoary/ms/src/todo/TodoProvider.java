package com.todoary.ms.src.todo;

import com.todoary.ms.src.todo.dto.GetTodoByDateRes;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoProvider {

    private final TodoDao todoDao;

    @Autowired
    public TodoProvider(TodoDao todoDao) {
        this.todoDao = todoDao;
    }

    public boolean checkUsersTodoById(long userId, long todoId) throws BaseException {
        try {
            return (todoDao.selectExistsUsersTodoById(userId, todoId) == 1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<GetTodoByDateRes> retrieveTodoListByDate(long userId, String targetDate) throws BaseException {
        try {
            return todoDao.selectTodoListByDate(userId, targetDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void assertUsersTodoValidById(long userId, long todoId) throws BaseException {
        if (!checkUsersTodoById(userId, todoId))
            throw new BaseException(BaseResponseStatus.USERS_TODO_NOT_EXISTS);
    }
}
