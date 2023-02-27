package com.todoary.ms.src.web.dto.alarm;


import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AlarmResponse {


    private Boolean toDoAlarmEnable;

    private Boolean remindAlarmEnable;

    private Boolean dailyAlarmEnable;
}
