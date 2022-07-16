package com.todoary.ms.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetUserRes {
    private Long id;
    private String nickname;
    private String email;
    private String profile_img_url;
    private String introduce;
}
