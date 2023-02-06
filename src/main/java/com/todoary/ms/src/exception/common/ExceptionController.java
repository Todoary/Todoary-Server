package com.todoary.ms.src.exception.common;

import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import com.todoary.ms.util.ErrorLogWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

import static com.todoary.ms.util.BaseResponseStatus.INTERNAL_SERVER_ERROR;

// 예외가 발생했을 때 json 형태로 반환할 때 사용하는 어노테이션
@RestControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler(TodoaryException.class)
    private ResponseEntity<BaseResponse<BaseResponseStatus>> handleTodoaryException(TodoaryException exception, HttpServletRequest httpServletRequest) {
        ErrorLogWriter.writeExceptionWithRequest(exception, httpServletRequest);
        return ResponseEntity.ok()
                .body(BaseResponse.from(exception.getStatus()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<BaseResponse<BaseResponseStatus>> handleBadRequest(MethodArgumentNotValidException exception, HttpServletRequest httpServletRequest) {
        ErrorLogWriter.writeExceptionWithRequest(exception, httpServletRequest);
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String enumName = fieldError.getDefaultMessage();
        return ResponseEntity.ok()
                .body(BaseResponse.from(BaseResponseStatus.valueOf(enumName)));
    }

    @ExceptionHandler({Exception.class})
    private ResponseEntity<BaseResponse<BaseResponseStatus>> handleServerException(Exception exception, HttpServletRequest httpServletRequest) {
        ErrorLogWriter.writeExceptionWithRequest(exception, httpServletRequest);
        return ResponseEntity.ok()
                .body(BaseResponse.from(INTERNAL_SERVER_ERROR));
    }
}
