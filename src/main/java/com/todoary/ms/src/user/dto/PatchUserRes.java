package com.todoary.ms.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatchUserRes {
    private String nickname;
    private String introduce;
}
