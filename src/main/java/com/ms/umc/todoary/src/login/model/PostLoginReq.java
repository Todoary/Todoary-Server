package com.ms.umc.todoary.src.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLoginReq {
    private String email;
    private String password;
    private boolean _isAutoLoginChecked;
}
