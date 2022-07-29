package com.todoary.ms.src.todo;

import com.todoary.ms.src.category.CategoryProvider;
import com.todoary.ms.src.todo.dto.PostTodoReq;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
    public Long createTodo(Long userId, PostTodoReq postTodoReq) throws BaseException {
        categoryProvider.assertUsersCategoriesValidById(userId, postTodoReq.getCategories());
        try {
            Long todoId;
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

    @Transactional(rollbackOn = Exception.class)
    public void modifyTodo(Long userId, Long todoId, PostTodoReq postTodoReq) throws BaseException {
        todoProvider.assertUsersTodoValidById(userId, todoId);
        try {
            todoDao.updateTodo(todoId, postTodoReq);
            todoDao.deleteAndUpdateTodoCategories(todoId, postTodoReq.getCategories());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void removeTodo(Long userId, Long todoId) throws BaseException {
        todoProvider.assertUsersTodoValidById(userId, todoId);
        try {
            todoDao.deleteTodo(todoId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void modifyTodoCheck(Long userId, Long todoId, boolean isChecked) throws BaseException {
        todoProvider.assertUsersTodoValidById(userId, todoId);
        try {
            todoDao.updateTodoCheck(todoId, isChecked);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void modifyTodoPin(Long userId, Long todoId, boolean isPinned) throws BaseException {
        todoProvider.assertUsersTodoValidById(userId, todoId);
        try {
            todoDao.updateTodoPin(todoId, isPinned);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
