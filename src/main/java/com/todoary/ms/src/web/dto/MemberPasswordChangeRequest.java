package com.todoary.ms.src.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberPasswordChangeRequest {
    private String email;
    private String newPassword;
}
