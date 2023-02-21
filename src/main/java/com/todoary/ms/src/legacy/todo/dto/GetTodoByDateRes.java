package com.todoary.ms.src.legacy.todo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"todoId", "isPinned", "isChecked", "title", "targetDate", "isAlarmEnabled", "targetTime", "createdTime", "categoryId", "categoryTitle", "color" })
@JsonIgnoreProperties({"pinned", "checked", "alarmEnabled"}) // 중복 방지
public class GetTodoByDateRes {
    private Long todoId;
    @JsonProperty("isPinned")
    private boolean isPinned;
    @JsonProperty("isChecked")
    private boolean isChecked;
    private String title;
    private String targetDate;
    @JsonProperty("isAlarmEnabled")
    private boolean isAlarmEnabled;
    private String targetTime;
    private String createdTime;
    private Long categoryId;
    private String categoryTitle;
    private Integer color;
}
