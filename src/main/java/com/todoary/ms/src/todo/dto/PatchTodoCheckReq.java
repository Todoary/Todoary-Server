package com.todoary.ms.src.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PatchTodoCheckReq {
    private Long todoId;
    @JsonProperty("isChecked")
    private boolean isChecked;
}
