package com.todoary.ms.src.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileUpdateResponse {
    private String nickname;
    private String introduce;
}
