package com.todoary.ms.src.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TodoUpdateAlarmRequest {
    @JsonProperty("isAlarmEnabled")
    private boolean isAlarmEnabled;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetDate;

    @DateTimeFormat(pattern = "kk:mm")
    private LocalTime targetTime;
}
