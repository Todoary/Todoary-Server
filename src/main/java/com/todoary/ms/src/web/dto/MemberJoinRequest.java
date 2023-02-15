package com.todoary.ms.src.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberJoinRequest {
    private String name;
    private String nickname;
    private String email;
    private String password;
    @JsonProperty("isTermsEnable")
    private boolean isTermsEnable;
}
