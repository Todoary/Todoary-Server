package com.todoary.ms.src.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.todoary.ms.src.auth.model.Token;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isNewUser", "name", "email","provider","token"})
@JsonIgnoreProperties({"newUser", "deleted"}) // newUser/isNewUser 중복 방지
public class GetAppleUserRes {

    @JsonProperty("isNewUser")
    private boolean isNewUser;
    private String name;
    private String email;
    private Token token;
}