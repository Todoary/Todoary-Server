package com.todoary.ms.src.legacy.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PatchTodoPinReq {
    private Long todoId;
    @JsonProperty("isPinned")
    private boolean isPinned;
}
