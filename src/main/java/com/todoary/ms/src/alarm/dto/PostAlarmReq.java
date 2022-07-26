package com.todoary.ms.src.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostAlarmReq {
    private String targetToken;
    private String title;
    private String body;
}
