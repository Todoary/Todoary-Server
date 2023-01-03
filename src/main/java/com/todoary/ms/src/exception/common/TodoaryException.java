package com.todoary.ms.src.exception.common;

import com.todoary.ms.util.BaseResponseStatus;
import lombok.Getter;

@Getter
public class TodoaryException extends RuntimeException{
    private BaseResponseStatus status;

    public TodoaryException(BaseResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
