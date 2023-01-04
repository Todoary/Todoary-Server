package com.todoary.ms.src.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todoary.ms.src.service.JpaTodoService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TodoSaveRequest {
    private String title;
    @JsonProperty("isAlarmEnabled")
    private boolean isAlarmEnabled;
    private String targetDate;
    private String targetTime;
    private Long categoryId;

    public Todo toEntity(Member member, Category category) {
        return Todo.builder()
                .title(getTitle())
                .isAlarmEnabled(isAlarmEnabled())
                .category(category)
                .member(member)
                .targetDate(JpaTodoService.convertToLocalDate(getTargetDate()))
                .targetTime(JpaTodoService.convertToLocalTime(getTargetTime()))
                .build();
    }
}
