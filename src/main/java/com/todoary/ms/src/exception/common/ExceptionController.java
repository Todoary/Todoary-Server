package com.todoary.ms.src.exception.common;

import com.todoary.ms.util.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 예외가 발생했을 때 json 형태로 반환할 때 사용하는 어노테이션
@RestControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler(TodoaryException.class)
    public ResponseEntity<BaseResponse> handleTodoaryException(final TodoaryException exception) {
        return ResponseEntity.ok()
                .body(new BaseResponse<>(exception.getStatus()));
    }
}
