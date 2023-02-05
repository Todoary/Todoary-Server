package com.todoary.ms.src.exception.common;

import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import com.todoary.ms.util.ErrorLogWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

import java.util.Objects;
import java.util.Optional;

import static com.todoary.ms.util.BaseResponseStatus.*;

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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<BaseResponse<BaseResponseStatus>> handleBadRequest(MethodArgumentTypeMismatchException exception, HttpServletRequest httpServletRequest) {
        ErrorLogWriter.writeExceptionWithRequest(exception, httpServletRequest);
        String typeName = Optional.ofNullable(exception.getRequiredType())
                .map(Class::getSimpleName)
                .orElse("");
        if (typeName.equals("LocalDate")){
            return ResponseEntity.ok()
                    .body(BaseResponse.from(ILLEGAL_DATE));
        }
        return ResponseEntity.ok()
                .body(BaseResponse.from(ILLEGAL_ARGUMENT));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<BaseResponse<BaseResponseStatus>> handleBadRequest(HttpMessageNotReadableException exception, HttpServletRequest httpServletRequest) {
        ErrorLogWriter.writeExceptionWithRequest(exception, httpServletRequest);
        return ResponseEntity.ok()
                .body(BaseResponse.from(ILLEGAL_ARGUMENT));
    }

    @ExceptionHandler({Exception.class})
    private ResponseEntity<BaseResponse<BaseResponseStatus>> handleServerException(Exception exception, HttpServletRequest httpServletRequest) {
        ErrorLogWriter.writeExceptionWithRequest(exception, httpServletRequest);
        return ResponseEntity.ok()
                .body(BaseResponse.from(INTERNAL_SERVER_ERROR));
    }
}
