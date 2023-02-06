package com.todoary.ms.src.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TodoAlarmRequest {
    @NotNull(message = "NULL_ARGUMENT")
    private Boolean isAlarmEnabled;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message="EMPTY_TODO_DATE")
    private LocalDate targetDate;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime targetTime;
}
