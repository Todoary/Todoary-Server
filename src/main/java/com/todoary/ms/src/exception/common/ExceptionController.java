package com.todoary.ms.src.exception.common;

import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import com.todoary.ms.util.ErrorLogWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

// 예외가 발생했을 때 json 형태로 반환할 때 사용하는 어노테이션
@RestControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler(TodoaryException.class)
    private ResponseEntity<BaseResponse> handleTodoaryException(TodoaryException exception, HttpServletRequest httpServletRequest) {
        ErrorLogWriter.writeExceptionWithRequest(exception, httpServletRequest);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(exception.getStatus()));
    }

    @ExceptionHandler({ Exception.class })
    private ResponseEntity handleServerException(Exception exception, HttpServletRequest httpServletRequest) {
        ErrorLogWriter.writeExceptionWithRequest(exception, httpServletRequest);
        return ResponseEntity.ok()
                .body(new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR));
    }
}
