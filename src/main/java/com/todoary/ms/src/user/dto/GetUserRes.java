package com.todoary.ms.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetUserRes {
    private String profileImgUrl;
    private String nickname;
    private String introduce;
    private String email;
}
