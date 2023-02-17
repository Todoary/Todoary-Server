package com.todoary.ms.src.web.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlarmEnablesResponse {
    private Long memberId;
    private Boolean toDoAlarmEnable;
    private Boolean remindAlarmEnable;
    private Boolean dailyAlarmEnable;
}
