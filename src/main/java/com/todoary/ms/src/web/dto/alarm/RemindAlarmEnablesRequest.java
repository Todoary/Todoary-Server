package com.todoary.ms.src.web.dto.alarm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RemindAlarmEnablesRequest {
    private boolean isChecked;
}
