package com.todoary.ms.src.todo;

import com.todoary.ms.src.alarm.AlarmService;
import com.todoary.ms.src.todo.dto.*;
import com.todoary.ms.src.todo.dto.PostTodoReq;
import com.todoary.ms.src.todo.dto.PostTodoRes;
import com.todoary.ms.src.user.UserProvider;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/todo")
public class TodoController {

    private final TodoProvider todoProvider;
    private final TodoService todoService;
    private final UserProvider userProvider;
    private final AlarmService alarmService;

    @Autowired
    public TodoController(TodoProvider todoProvider, TodoService todoService, UserProvider userProvider, AlarmService alarmService) {
        this.todoProvider = todoProvider;
        this.todoService = todoService;
        this.userProvider = userProvider;
        this.alarmService = alarmService;
    }

    private Long getUserIdFromRequest(HttpServletRequest request) throws BaseException {
        Long userId = Long.parseLong(request.getAttribute("user_id").toString());
        userProvider.assertUserValidById(userId);
        return userId;
    }

    /**
     * 3.1 투두 생성 api
     * [POST] /todo
     *
     * @param postTodoReq
     * @return BaseResponseStatus
     */
    @PostMapping("")
    public BaseResponse<PostTodoRes> postTodo(HttpServletRequest request, @RequestBody PostTodoReq postTodoReq) {
        try {
            Long userId = getUserIdFromRequest(request);
            Long todoId = todoService.createTodo(userId, postTodoReq);
            if (postTodoReq.isAlarmEnabled())
                alarmService.createAlarmTodo(userId, todoId);
            return new BaseResponse<>(new PostTodoRes(todoId));
        } catch (BaseException e) {
            log.warn(e.getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 3.2 투두 수정 api
     * [PATCH] /todo/:todoId
     *
     * @param request
     * @param postTodoReq
     * @return
     */
    @PatchMapping("/{todoId}")
    public BaseResponse<BaseResponseStatus> patchTodo(HttpServletRequest request,
                                                      @PathVariable("todoId") Long todoId,
                                                      @RequestBody PostTodoReq postTodoReq) {
        try {
            Long userId = getUserIdFromRequest(request);
            todoService.modifyTodo(userId, todoId, postTodoReq);
            if (!postTodoReq.isAlarmEnabled())
                alarmService.modifyAlarmTodo(userId, todoId);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            log.warn(e.getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 3.3 투두 삭제 api
     * [DELETE] /todo/:todoId
     *
     * @param request
     * @param todoId
     * @return
     */
    @DeleteMapping("/{todoId}")
    public BaseResponse<BaseResponseStatus> deleteTodoById(HttpServletRequest request, @PathVariable("todoId") Long todoId) {
        try {
            Long userId = getUserIdFromRequest(request);
            todoService.removeTodo(userId, todoId);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            log.warn(e.getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 3.4 투두 날짜별 조회 api
     * [GET] /todo/date/:date
     *
     * @param request
     * @param targetDate
     * @return
     */
    @GetMapping("/date/{date}")
    public BaseResponse<List<GetTodoByDateRes>> getTodoListByDate(HttpServletRequest request,
                                                                  @PathVariable("date") String targetDate) {
        try {
            Long userId = getUserIdFromRequest(request);
            return new BaseResponse<>(todoProvider.retrieveTodoListByDate(userId, targetDate));
        } catch (BaseException e) {
            log.warn(e.getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 3.5 투두 카테고리별 조회 api
     * [GET] /todo/category/:categoryId
     *
     * @param request
     * @param categoryId
     * @return
     */
    @GetMapping("/category/{categoryId}")
    public BaseResponse<List<GetTodoByCategoryRes>> getTodoListByCategory(HttpServletRequest request,
                                                                          @PathVariable("categoryId") Long categoryId) {
        try {
            Long userId = getUserIdFromRequest(request);
            return new BaseResponse<>(todoProvider.retrieveTodoListByCategory(userId, categoryId));
        } catch (BaseException e) {
            log.warn(e.getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 3.6 투두 체크박스 체크 api
     * [PATCH] /todo/status
     *
     * @param request
     * @param patchTodoCheckReq
     * @return
     */
    @PatchMapping("/check")
    public BaseResponse<BaseResponseStatus> patchTodoCheck(HttpServletRequest request,
                                                           @RequestBody PatchTodoCheckReq patchTodoCheckReq) {
        try {
            Long userId = getUserIdFromRequest(request);
            todoService.modifyTodoCheck(userId, patchTodoCheckReq.getTodoId(), patchTodoCheckReq.isChecked());
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            log.warn(e.getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PatchMapping("/pin")
    public BaseResponse<BaseResponseStatus> patchTodoPin(HttpServletRequest request,
                                                         @RequestBody PatchTodoPinReq patchTodoPinReq) {
        try {
            Long userId = getUserIdFromRequest(request);
            todoService.modifyTodoPin(userId, patchTodoPinReq.getTodoId(), patchTodoPinReq.isPinned());
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            log.warn(e.getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }
}
