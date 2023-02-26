package com.todoary.ms.src.legacy.todo;

import com.todoary.ms.src.legacy.category.LegacyCategoryProvider;
import com.todoary.ms.src.legacy.todo.dto.PatchTodoAlarmReq;
import com.todoary.ms.src.legacy.todo.dto.PostTodoReq;
import com.todoary.ms.src.legacy.BaseException;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class LegacyTodoService {
    private final LegacyTodoProvider legacyTodoProvider;
    private final LegacyTodoDao legacyTodoDao;
    private final LegacyCategoryProvider categoryProvider;

    @Autowired
    public LegacyTodoService(LegacyTodoProvider legacyTodoProvider, LegacyTodoDao legacyTodoDao, LegacyCategoryProvider categoryProvider) {
        this.legacyTodoProvider = legacyTodoProvider;
        this.legacyTodoDao = legacyTodoDao;
        this.categoryProvider = categoryProvider;
    }

    @Transactional(rollbackOn = Exception.class)
    public Long createTodo(Long userId, PostTodoReq postTodoReq) throws BaseException {
        categoryProvider.assertUsersCategoryValidById(userId, postTodoReq.getCategoryId());
        try {
            Long todoId;
            if (postTodoReq.isAlarmEnabled()) {
                todoId = legacyTodoDao.insertTodo(userId,postTodoReq.getCategoryId(), postTodoReq.getTitle(), postTodoReq.getTargetDate(), postTodoReq.isAlarmEnabled(), postTodoReq.getTargetTime());
            } else {
                todoId = legacyTodoDao.insertTodo(userId,postTodoReq.getCategoryId(), postTodoReq.getTitle(), postTodoReq.getTargetDate());
            }
            //todoDao.insertTodoCategories(todoId, postTodoReq.getCategoryId());
            return todoId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void modifyTodo(Long userId, Long todoId, PostTodoReq postTodoReq) throws BaseException {
        legacyTodoProvider.assertUsersTodoValidById(userId, todoId);
        try {
            legacyTodoDao.updateTodo(todoId, postTodoReq);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void removeTodo(Long userId, Long todoId) throws BaseException {
        legacyTodoProvider.assertUsersTodoValidById(userId, todoId);
        try {
            legacyTodoDao.deleteTodo(todoId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void modifyTodoCheck(Long userId, Long todoId, boolean isChecked) throws BaseException {
        legacyTodoProvider.assertUsersTodoValidById(userId, todoId);
        try {
            legacyTodoDao.updateTodoCheck(todoId, isChecked);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void modifyTodoPin(Long userId, Long todoId, boolean isPinned) throws BaseException {
        legacyTodoProvider.assertUsersTodoValidById(userId, todoId);
        try {
            legacyTodoDao.updateTodoPin(todoId, isPinned);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void modifyTodoAlarm(Long userId, Long todoId, PatchTodoAlarmReq patchTodoAlarmReq) throws BaseException {
        legacyTodoProvider.assertUsersTodoValidById(userId, todoId);
        try {
            legacyTodoDao.updateTodoAlarm(todoId, patchTodoAlarmReq);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
