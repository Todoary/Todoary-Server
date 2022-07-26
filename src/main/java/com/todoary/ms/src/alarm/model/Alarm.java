package com.todoary.ms.src.alarm.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Alarm {
    private Long user_id;
    private String registration_token;
    private String title;
    private String body;
    private Date alarmDateTime;
}
