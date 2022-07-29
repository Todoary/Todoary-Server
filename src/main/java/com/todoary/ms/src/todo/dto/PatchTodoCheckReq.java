package com.todoary.ms.src.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PatchTodoCheckReq {
    private Long todoId;
    @JsonProperty("isChecked")
    private boolean isChecked;
}