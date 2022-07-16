package com.ms.umc.todoary.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUserRes {
    private String jwt;
    private int userIdx;
}
