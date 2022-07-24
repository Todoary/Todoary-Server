package com.todoary.ms.src.todo;

import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
