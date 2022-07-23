package com.todoary.ms.src.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostTodoReq {
    private String title;
    private String targetDate;
    @JsonProperty("isAlarmEnabled")
    private boolean isAlarmEnabled;
    private String targetTime;
    private List<Integer> categories;
}
