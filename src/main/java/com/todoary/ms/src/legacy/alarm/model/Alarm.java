package com.todoary.ms.src.legacy.alarm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class Alarm {
    private String fcm_token;
    private String title;
    private String target_date;
    private String target_time;

    public Alarm(String fcm_token) {
        this.fcm_token = fcm_token;
    }
}
