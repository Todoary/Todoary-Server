package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.config.auth.LoginMember;
import com.todoary.ms.src.service.JpaTodoService;
import com.todoary.ms.src.todo.dto.PostTodoRes;
import com.todoary.ms.src.web.dto.TodoRequest;
import com.todoary.ms.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
}
