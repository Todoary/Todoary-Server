package com.todoary.ms.src.todo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.todoary.ms.src.category.model.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"todoId", "isChecked", "title", "targetDate", "isAlarmEnabled", "targetTime", "createdTime", "categoryId", "categoryTitle", "color"})
@JsonIgnoreProperties({"checked", "alarmEnabled"}) // 중복 방지
public class GetTodoByCategoryRes {
    private Long todoId;
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
