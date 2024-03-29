package com.todoary.ms.src.common.exception;

import com.amazonaws.AmazonServiceException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.todoary.ms.src.common.response.BaseResponse;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Optional;

import static com.todoary.ms.src.common.response.BaseResponseStatus.*;

// 예외가 발생했을 때 json 형태로 반환할 때 사용하는 어노테이션
@RestControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler(TodoaryException.class)
    private ResponseEntity<BaseResponse<BaseResponseStatus>> handleTodoaryException(TodoaryException exception) {
        return ResponseEntity.ok()
                .body(BaseResponse.from(exception.getStatus()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<BaseResponse<BaseResponseStatus>> handleBadRequest(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String enumName = fieldError.getDefaultMessage();
        return ResponseEntity.ok()
                .body(BaseResponse.from(BaseResponseStatus.valueOf(enumName)));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<BaseResponse<BaseResponseStatus>> handleBadRequest(MethodArgumentTypeMismatchException exception) {
        String typeName = Optional.ofNullable(exception.getRequiredType())
                .map(Class::getSimpleName)
                .orElse("");
        if (isDateType(typeName)) {
            return ResponseEntity.ok()
                    .body(BaseResponse.from(ILLEGAL_DATETIME));
        }
        return ResponseEntity.ok()
                .body(BaseResponse.from(ILLEGAL_ARGUMENT));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<BaseResponse<BaseResponseStatus>> handleBadRequest(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof InvalidFormatException) {
            String simpleName = ((InvalidFormatException) cause).getTargetType().getSimpleName();
            if (isDateType(simpleName)) {
                return ResponseEntity.ok()
                        .body(BaseResponse.from(ILLEGAL_DATETIME));
            }
        }
        return ResponseEntity.ok()
                .body(BaseResponse.from(ILLEGAL_ARGUMENT));
    }

    @ExceptionHandler(AmazonServiceException.class)
    private ResponseEntity<BaseResponse<BaseResponseStatus>> handleS3Request(AmazonServiceException exception) {
        return ResponseEntity.ok()
                .body(BaseResponse.from(AWS_ACCESS_DENIED));
    }

    @ExceptionHandler({Exception.class})
    private ResponseEntity<BaseResponse<BaseResponseStatus>> handleServerException(Exception exception) {
        return ResponseEntity.ok()
                .body(BaseResponse.from(INTERNAL_SERVER_ERROR));
    }

    private boolean isDateType(String simpleName) {
        return simpleName.equals("LocalDate") || simpleName.equals("YearMonth") || simpleName.equals("LocalTime");
    }
}
