package com.todoary.ms.src.common.event.handler;

import com.todoary.ms.src.common.event.DiaryCreatedEvent;
import com.todoary.ms.src.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
@Slf4j
public class DiaryEventHandler {
    private final AlarmService alarmService;

    @EventListener
    public void updateMembersRemindAlarm(DiaryCreatedEvent event) {
        LocalDate alarmTargetDate = event.getDiaryDate().plusDays(7);
        alarmService.updateRemindAlarmToDate(event.getMemberId(), alarmTargetDate);
        log.info("Remind Alarm Updated - {} alarmDate as [{}]", event, alarmTargetDate);
    }
}
