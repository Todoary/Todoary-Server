package com.todoary.ms.src.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostProfileImgRes {
    private Long user_id;
    private String profile_img_url;
}
