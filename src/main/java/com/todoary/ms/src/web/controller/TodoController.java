package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.common.auth.annotation.LoginMember;
import com.todoary.ms.src.service.todo.TodoService;
import com.todoary.ms.src.web.dto.common.PageResponse;
import com.todoary.ms.src.web.dto.todo.TodoAlarmRequest;
import com.todoary.ms.src.web.dto.todo.TodoRequest;
import com.todoary.ms.src.web.dto.todo.TodoResponse;
import com.todoary.ms.src.web.dto.todo.TodoSaveResponse;
import com.todoary.ms.src.common.response.BaseResponse;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import lombok.*;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static com.todoary.ms.src.common.response.BaseResponseStatus.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todo")
public class TodoController {
    private final TodoService todoService;

    // 3.1 투두 생성 api
    @PostMapping("")
    public BaseResponse<TodoSaveResponse> createTodo(
            @LoginMember Long memberId,
            @RequestBody @Valid TodoRequest request
    ) {
        Long todoId = todoService.createMembersTodo(memberId, request);
        return new BaseResponse<>(new TodoSaveResponse(todoId));
    }

    // 3.2 투두 수정
    @PatchMapping("/{todoId}")
    public BaseResponse<TodoResponse> modifyTodo(
            @LoginMember Long memberId,
            @PathVariable("todoId") Long todoId,
            @RequestBody @Valid TodoRequest request
    ) {
        return new BaseResponse<>(todoService.updateMembersTodo(memberId, todoId, request));
    }

    // 3.3 투두 삭제
    @DeleteMapping("/{todoId}")
    public BaseResponse<BaseResponseStatus> deleteTodo(
            @LoginMember Long memberId,
            @PathVariable("todoId") Long todoId
    ) {
        todoService.deleteMembersTodo(memberId, todoId);
        return BaseResponse.from(SUCCESS);
    }

    // 3.4 투두 날짜별 조회
    @GetMapping("/date/{date}")
    public BaseResponse<List<TodoResponse>> retrieveTodosByDate(
            @LoginMember Long memberId,
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate targetDate
    ) {
        List<TodoResponse> todos = todoService.retrieveMembersTodosOnDate(memberId, targetDate);
        return new BaseResponse<>(todos);
    }

    // 3.5 투두 카테고리별 조회
    // * 오늘 날짜 이전은 포함하지 않는다
    @GetMapping("/category/{categoryId}")
    public BaseResponse<List<TodoResponse>> retrieveTodosByCategoryStartingToday(
            @LoginMember Long memberId,
            @PathVariable("categoryId") Long categoryId
    ) {
        List<TodoResponse> todos = todoService.retrieveMembersTodosByCategory(memberId, categoryId);
        return new BaseResponse<>(todos);
    }

    // 3.6 투두 체크박스 체크 상태 변경
    @PatchMapping("/check")
    public BaseResponse<BaseResponseStatus> markTodoAsDone(
            @LoginMember Long memberId,
            @RequestBody @Valid MarkTodoRequest request
    ) {
        todoService.updateMembersTodoMarkedStatus(memberId, request.getTodoId(), request.getIsChecked());
        return BaseResponse.from(SUCCESS);
    }

    // 3.7 투두 핀 고정 상태 변경
    @PatchMapping("/pin")
    public BaseResponse<BaseResponseStatus> pinTodo(
            @LoginMember Long memberId,
            @RequestBody @Valid PinTodoRequest request
    ) {
        todoService.updateMembersTodoPinnedStatus(memberId, request.getTodoId(), request.getIsPinned());
        return BaseResponse.from(SUCCESS);
    }

    // 3.8 월별 투두 존재 날짜 조회
    @GetMapping("/days/{yearMonth}")
    public BaseResponse<List<Integer>> retrieveDaysHavingTodoInMonth(
            @LoginMember Long memberId,
            @PathVariable("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth
    ) {
        List<Integer> days = todoService.retrieveDaysHavingTodoOfMemberInMonth(memberId, yearMonth);
        return new BaseResponse<>(days);
    }

    // 3.9 투두 알람 수정
    @PatchMapping("/{todoId}/alarm")
    public BaseResponse<BaseResponseStatus> modifyTodoAlaram(
            @LoginMember Long memberId,
            @PathVariable("todoId") Long todoId,
            @RequestBody @Valid TodoAlarmRequest request
    ) {
        todoService.updateMembersTodoAlarm(memberId, todoId, request);
        return BaseResponse.from(SUCCESS);
    }

    // 3.10 페이징 적용하여 카테고리별 투두 조회
    // * 오늘 날짜 이전은 포함하지 않는다
    // ?page=0&size=5
    // 만약 파라미터가 넘어오지 않으면 기본값으로 세팅됨(Page request [number: 0, size 20, sort: UNSORTED])
    @GetMapping("/category/{categoryId}/page")
    public BaseResponse<PageResponse<TodoResponse>> retrieveTodoPageByCategoryStartingToday(
            @LoginMember Long memberId,
            @PathVariable("categoryId") Long categoryId,
            Pageable pageable
    ) {
        return new BaseResponse<>(todoService.findTodoPageByCategory(pageable, memberId, categoryId));
    }

    @ToString
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class MarkTodoRequest {
        @NotNull(message = "NULL_ARGUMENT")
        private Long todoId;
        @NotNull(message = "NULL_ARGUMENT")
        private Boolean isChecked;
    }

    @ToString
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class PinTodoRequest {
        @NotNull(message = "NULL_ARGUMENT")
        private Long todoId;
        @NotNull(message = "NULL_ARGUMENT")
        private Boolean isPinned;
    }
}
