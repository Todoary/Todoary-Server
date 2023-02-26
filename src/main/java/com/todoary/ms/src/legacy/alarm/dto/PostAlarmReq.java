package com.todoary.ms.src.legacy.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAlarmReq {
    private String fcm_token;
    private String title;
    private String body;
}
