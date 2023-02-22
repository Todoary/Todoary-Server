package com.todoary.ms.src.legacy.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatchUserRes {
    private String nickname;
    private String introduce;
}
