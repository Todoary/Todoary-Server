package com.todoary.ms.src.todo;

import com.todoary.ms.src.todo.dto.GetTodoByDateRes;
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

    @Autowired
    public TodoController(TodoProvider todoProvider, TodoService todoService, UserProvider userProvider) {
        this.todoProvider = todoProvider;
        this.todoService = todoService;
        this.userProvider = userProvider;
    }

    private long getUserIdFromRequest(HttpServletRequest request) throws BaseException {
        long userId = Long.parseLong(request.getAttribute("user_id").toString());
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
            long userId = getUserIdFromRequest(request);
            long todoId = todoService.createTodo(userId, postTodoReq);
            return new BaseResponse<>(new PostTodoRes(todoId));
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
    public BaseResponse<BaseResponseStatus> deleteTodoById(HttpServletRequest request, @PathVariable("todoId") long todoId) {
        try {
            long userId = getUserIdFromRequest(request);
            todoService.removeTodo(userId, todoId);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            log.warn(e.getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 3.4 투두 날짜별 조회 api
     * [GET] /todo?date=
     *
     * @param request
     * @param targetDate
     * @return
     */
    @GetMapping("")
    public BaseResponse<List<GetTodoByDateRes>> getTodoListByDate(HttpServletRequest request,
                                                                  @RequestParam("date") String targetDate) {
        try {
            long userId = getUserIdFromRequest(request);
            return new BaseResponse<>(todoProvider.retrieveTodoListByDate(userId, targetDate));
        } catch (BaseException e) {
            log.warn(e.getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }
}
