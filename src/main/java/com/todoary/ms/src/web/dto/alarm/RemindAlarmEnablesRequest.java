package com.todoary.ms.src.web.dto.alarm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RemindAlarmEnablesRequest {
    @JsonProperty("isChecked")
    private boolean isChecked;
}
