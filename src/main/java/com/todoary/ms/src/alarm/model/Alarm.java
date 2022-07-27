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
}
