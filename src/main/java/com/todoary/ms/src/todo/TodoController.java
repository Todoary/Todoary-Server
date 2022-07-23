package com.todoary.ms.src.todo;

import com.todoary.ms.src.todo.dto.PostTodoReq;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/todo")
public class TodoController {
    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 3.1 투두 생성 api
     * [POST] /todo
     *
     * @param postTodoReq
     * @return BaseResponseStatus
     */
    @PostMapping("")
    public BaseResponse<BaseResponseStatus> postTodo(HttpServletRequest request, @RequestBody PostTodoReq postTodoReq) {
        try {
            long userId = getUserIdFromRequest(request);
            todoService.createTodo(userId, postTodoReq);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            log.warn(e.getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }

    private long getUserIdFromRequest(HttpServletRequest request){
        return Long.parseLong(request.getAttribute("user_id").toString());
    }
}
