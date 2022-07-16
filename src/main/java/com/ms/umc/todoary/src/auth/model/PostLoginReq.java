package com.ms.umc.todoary.src.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLoginReq {
    private String email;
    private String password;
    // 시작 is 안되는 거 때문인가?
    @JsonProperty("isAutoLoginChecked")
    private boolean isAutoLoginChecked;
}
