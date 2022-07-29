package com.todoary.ms.src.alarm.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Alarm {
    private String registration_token;
    private String title;
    private String target_date;
    private String target_time;

    public Alarm(String registration_token) {
        this.registration_token = registration_token;
    }
}
