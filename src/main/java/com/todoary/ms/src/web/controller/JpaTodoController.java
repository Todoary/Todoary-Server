package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.config.auth.LoginMember;
import com.todoary.ms.src.service.JpaTodoService;
import com.todoary.ms.src.todo.dto.PostTodoRes;
import com.todoary.ms.src.web.dto.TodoRequest;
import com.todoary.ms.src.web.dto.TodoResponse;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import static com.todoary.ms.util.BaseResponseStatus.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/todo")
public class JpaTodoController {
    private final JpaTodoService todoService;

    // 3.1 투두 생성 api
    @PostMapping("")
    public BaseResponse<PostTodoRes> createTodo(
            @LoginMember Long memberId,
            @RequestBody @Valid TodoRequest request
    ) {
        Long todoId = todoService.saveTodo(memberId, request);
        return new BaseResponse<>(new PostTodoRes(todoId));
    }

    // 3.2 투두 수정
    @PatchMapping("/{todoId}")
    public BaseResponse<BaseResponseStatus> modifyTodo(
            @LoginMember Long memberId,
            @PathVariable("todoId") Long todoId,
            @RequestBody @Valid TodoRequest request
    ) {
        todoService.updateTodo(memberId, todoId, request);
        return BaseResponse.from(SUCCESS);
    }

    // 3.3 투두 삭제
    @DeleteMapping("/{todoId}")
    public BaseResponse<BaseResponseStatus> deleteTodo(
            @LoginMember Long memberId,
            @PathVariable("todoId") Long todoId
    ) {
        todoService.deleteTodo(memberId, todoId);
        return BaseResponse.from(SUCCESS);
    }

    // 3.4 투두 날짜별 조회
    @GetMapping("/date/{date}")
    public BaseResponse<List<TodoResponse>> retrieveTodosByDate(
            @LoginMember Long memberId,
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate targetDate
    ) {
        List<TodoResponse> todos = todoService.findTodosByDate(memberId, targetDate);
        return new BaseResponse<>(todos);
    }

    // 3.5 투두 카테고리별 조회
    // * 오늘 날짜 이전은 포함하지 않는다
    @GetMapping("/category/{categoryId}")
    public BaseResponse<List<TodoResponse>> retrieveTodosByCategoryStartingToday(
            @LoginMember Long memberId,
            @PathVariable("categoryId") Long categoryId
    ) {
        List<TodoResponse> todos = todoService.findTodosByCategoryStartingToday(memberId, categoryId);
        return new BaseResponse<>(todos);
    }
}
