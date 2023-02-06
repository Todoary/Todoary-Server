package com.todoary.ms.src.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.todoary.ms.src.domain.Todo;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@ToString
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class TodoResponse {
    private Long todoId;
    private Boolean isPinned;
    private Boolean isChecked;
    private String title;
    private Boolean isAlarmEnabled;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate targetDate;
    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime targetTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdTime;
    private Long categoryId;
    private String categoryTitle;
    private Integer color;

    @Builder
    public TodoResponse(Long todoId, boolean isPinned, boolean isChecked, String title, boolean isAlarmEnabled, LocalDate targetDate, LocalTime targetTime, LocalDateTime createdTime, Long categoryId, String categoryTitle, Integer color) {
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
                .targetDate(todo.getTargetDate())
                .targetTime(todo.getTargetTime())
                .createdTime(todo.getCreatedAt())
                .categoryId(todo.getCategory().getId())
                .categoryTitle(todo.getCategory().getTitle())
                .color(todo.getCategory().getColor().getCode())
                .build();
    }
}
