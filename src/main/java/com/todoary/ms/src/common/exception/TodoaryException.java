package com.todoary.ms.src.common.exception;

import com.todoary.ms.src.common.response.BaseResponseStatus;
import lombok.Getter;

@Getter
public class TodoaryException extends RuntimeException{
    private BaseResponseStatus status;

    public TodoaryException(BaseResponseStatus status) {
        super(status.name());
        this.status = status;
    }
}
