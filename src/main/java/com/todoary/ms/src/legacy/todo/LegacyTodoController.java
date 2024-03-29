package com.todoary.ms.src.legacy.todo;

import com.todoary.ms.src.common.response.BaseResponse;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import com.todoary.ms.src.common.util.ColumnLengthInfo;
import com.todoary.ms.src.common.util.ErrorLogWriter;
import com.todoary.ms.src.legacy.BaseException;
import com.todoary.ms.src.legacy.todo.dto.*;
import com.todoary.ms.src.legacy.user.LegacyUserProvider;
import com.todoary.ms.src.web.dto.todo.TodoSaveResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.todoary.ms.src.common.util.ErrorLogWriter.writeExceptionWithAuthorizedRequest;

@Slf4j
//@RestController
@RequestMapping("/todo")
public class LegacyTodoController {

    private final LegacyTodoProvider legacyTodoProvider;
    private final LegacyTodoService legacyTodoService;
    private final LegacyUserProvider legacyUserProvider;

    @Autowired
    public LegacyTodoController(LegacyTodoProvider legacyTodoProvider, LegacyTodoService legacyTodoService, LegacyUserProvider legacyUserProvider) {
        this.legacyTodoProvider = legacyTodoProvider;
        this.legacyTodoService = legacyTodoService;
        this.legacyUserProvider = legacyUserProvider;
    }

    private Long getUserIdFromRequest(HttpServletRequest request) throws BaseException {
        Long userId = Long.parseLong(request.getAttribute("user_id").toString());
        legacyUserProvider.assertUserValidById(userId);
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
    public BaseResponse<TodoSaveResponse> postTodo(HttpServletRequest request, @RequestBody PostTodoReq postTodoReq) {
        if (ColumnLengthInfo.getGraphemeLength(postTodoReq.getTitle()) > ColumnLengthInfo.TODO_TITLE_MAX_LENGTH)
            return new BaseResponse<>(BaseResponseStatus.DATA_TOO_LONG);
        try {
            Long userId = getUserIdFromRequest(request);
            Long todoId = legacyTodoService.createTodo(userId, postTodoReq);
            return new BaseResponse<>(new TodoSaveResponse(todoId));
        } catch (BaseException e) {
            ErrorLogWriter.writeExceptionWithAuthorizedRequest(e, request, postTodoReq.toString());
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
        if (ColumnLengthInfo.getGraphemeLength(postTodoReq.getTitle()) > ColumnLengthInfo.TODO_TITLE_MAX_LENGTH)
            return new BaseResponse<>(BaseResponseStatus.DATA_TOO_LONG);
        try {
            Long userId = getUserIdFromRequest(request);
            legacyTodoService.modifyTodo(userId, todoId, postTodoReq);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            ErrorLogWriter.writeExceptionWithAuthorizedRequest(e, request, postTodoReq.toString());
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
            legacyTodoService.removeTodo(userId, todoId);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            ErrorLogWriter.writeExceptionWithAuthorizedRequest(e, request);
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
            return new BaseResponse<>(legacyTodoProvider.retrieveTodoListByDate(userId, targetDate));
        } catch (BaseException e) {
            ErrorLogWriter.writeExceptionWithAuthorizedRequest(e, request);
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 3.5 투두 카테고리별 조회 api
     * - 오늘 날짜 이전은 포함하지 않음
     *
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
            return new BaseResponse<>(legacyTodoProvider.retrieveTodoListByCategory(userId, categoryId));
        } catch (BaseException e) {
            ErrorLogWriter.writeExceptionWithAuthorizedRequest(e, request);
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
            legacyTodoService.modifyTodoCheck(userId, patchTodoCheckReq.getTodoId(), patchTodoCheckReq.isChecked());
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request, patchTodoCheckReq.toString());
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 3.7 투두 핀 고정 변경 api
     * [PATCH] /todo/pin
     *
     * @param request
     * @param patchTodoPinReq
     * @return
     */
    @PatchMapping("/pin")
    public BaseResponse<BaseResponseStatus> patchTodoPin(HttpServletRequest request,
                                                         @RequestBody PatchTodoPinReq patchTodoPinReq) {
        try {
            Long userId = getUserIdFromRequest(request);
            legacyTodoService.modifyTodoPin(userId, patchTodoPinReq.getTodoId(), patchTodoPinReq.isPinned());
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request, patchTodoPinReq.toString());
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 3.8 월별 투두 존재 여부 조회 api
     * [GET] /days/:year-month
     *
     * @param request
     * @param yearAndMonth
     * @return
     */
    @GetMapping("/days/{yearAndMonth}")
    public BaseResponse<List<Integer>> getDaysInMonth(HttpServletRequest request,
                                                      @PathVariable("yearAndMonth") String yearAndMonth) {
        try {
            Long userId = getUserIdFromRequest(request);
            return new BaseResponse<>(legacyTodoProvider.retrieveDaysHavingTodoInMonth(userId, yearAndMonth));
        } catch (BaseException e) {
            ErrorLogWriter.writeExceptionWithAuthorizedRequest(e, request);
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 3.9 투두 알람 수정 api
     * [PATCH] /todo/:todoId/alarm
     */
    @PatchMapping("/{todoId}/alarm")
    public BaseResponse<BaseResponseStatus> patchTodoAlarm(HttpServletRequest request,
                                                      @PathVariable("todoId") Long todoId,
                                                      @RequestBody PatchTodoAlarmReq patchTodoAlarmReq) {
        try {
            Long userId = getUserIdFromRequest(request);
            legacyTodoService.modifyTodoAlarm(userId, todoId, patchTodoAlarmReq);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            ErrorLogWriter.writeExceptionWithAuthorizedRequest(e, request, patchTodoAlarmReq.toString());
            return new BaseResponse<>(e.getStatus());
        }
    }
}
