package com.todoary.ms.src.legacy.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PatchUserReq {
    private String nickname;
    private String introduce;
}
