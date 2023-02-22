package com.todoary.ms.src.legacy;

import com.todoary.ms.src.common.response.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseException extends Exception {
    private BaseResponseStatus status;

    @Override
    public String getMessage() {
        return status.getMessage();
    }
}
