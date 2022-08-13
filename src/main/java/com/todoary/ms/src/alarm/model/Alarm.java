package com.todoary.ms.src.alarm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class Alarm {
    private String registration_token;
    private String title;
    private String target_date;
    private String target_time;

    public Alarm(String registration_token) {
        this.registration_token = registration_token;
    }
}
