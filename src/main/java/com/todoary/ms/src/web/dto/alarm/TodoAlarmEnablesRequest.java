package com.todoary.ms.src.web.dto.alarm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoAlarmEnablesRequest {
    @JsonProperty("isChecked")
    private boolean isChecked;
}
