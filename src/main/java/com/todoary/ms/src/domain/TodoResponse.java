package com.todoary.ms.src.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.temporal.ChronoUnit;

@ToString
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoResponse {
    private Long todoId;
    @JsonProperty("isPinned")
    private boolean isPinned;
    @JsonProperty("isChecked")
    private boolean isChecked;
    private String title;
    @JsonProperty("isAlarmEnabled")
    private boolean isAlarmEnabled;
    private String targetDate;
    private String targetTime;
    private String createdTime;
    private Long categoryId;
    private String categoryTitle;
    private Integer color;

    @Builder
    public TodoResponse(Long todoId, boolean isPinned, boolean isChecked, String title, boolean isAlarmEnabled, String targetDate, String targetTime, String createdTime, Long categoryId, String categoryTitle, Integer color) {
        this.todoId = todoId;
        this.isPinned = isPinned;
        this.isChecked = isChecked;
        this.title = title;
        this.isAlarmEnabled = isAlarmEnabled;
        this.targetDate = targetDate;
        this.targetTime = targetTime;
        this.createdTime = createdTime;
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
        this.color = color;
    }

    public static TodoResponse from(Todo todo) {
        return TodoResponse.builder()
                .todoId(todo.getId())
                .isPinned(todo.getIsPinned())
                .isChecked(todo.getIsChecked())
                .title(todo.getTitle())
                .isAlarmEnabled(todo.getIsAlarmEnabled())
                .targetDate(todo.getTargetDate().toString())
                .targetTime(todo.getTargetTime().toString())
                .createdTime(todo.getCreatedAt().toLocalDate().toString() + " " + todo.getCreatedAt().toLocalTime().truncatedTo(ChronoUnit.SECONDS).toString())
                .categoryId(todo.getCategory().getId())
                .categoryTitle(todo.getCategory().getTitle())
                .color(todo.getCategory().getColor().getCode())
                .build();
    }
}
