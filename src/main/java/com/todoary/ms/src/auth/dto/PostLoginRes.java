package com.todoary.ms.src.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todoary.ms.src.auth.model.Token;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostLoginRes {

    @JsonProperty("isNew")
    private boolean isNew = false;
    private Token token;

    public PostLoginRes(Token token) {
        this.token = token;
    }
}
