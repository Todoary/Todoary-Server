package com.todoary.ms.src.web.dto.alarm;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class AlarmEnablesResponse {
    private Long memberId;
    private Boolean toDoAlarmEnable;
    private Boolean remindAlarmEnable;
    private Boolean dailyAlarmEnable;
}
