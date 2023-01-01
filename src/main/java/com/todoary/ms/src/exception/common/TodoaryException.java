package com.todoary.ms.src.exception.common;

import com.todoary.ms.util.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class TodoaryException extends RuntimeException{
    private BaseResponseStatus status;
}
