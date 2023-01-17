package com.todoary.ms.src.domain.token;

import com.todoary.ms.src.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccessToken {
    private String code;
}
