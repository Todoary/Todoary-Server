package com.todoary.ms.src.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TodoUpdateAlarmRequest {
    @JsonProperty("isAlarmEnabled")
    private boolean isAlarmEnabled;
    private String targetDate;
    private String targetTime;
}
