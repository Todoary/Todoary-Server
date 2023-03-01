package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.service.MemberService;
import com.todoary.ms.src.service.alarm.AlarmService;
import com.todoary.ms.src.service.alarm.FireBaseCloudMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class AlarmController {
    private final MemberService memberService;
    private final AlarmService alarmService;
    private final FireBaseCloudMessageService fireBaseCloudMessageService;

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void pushDailyAlarm(){
        log.info("Daily alarm triggered");

        List<Member> dailyAlarmEnabledMembers = memberService.findAllDailyAlarmEnabled();

        fireBaseCloudMessageService.sendDailyAlarm(dailyAlarmEnabledMembers);
    }

    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void pushTodoAlarm() {
        log.info("Todo alarm triggered");

        LocalTime now = LocalTime.now();
        LocalTime targetTime = LocalTime.of(now.getHour(), now.getMinute());

        fireBaseCloudMessageService.sendTodoAlarm(LocalDate.now(), targetTime);
    }

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void pushRemindAlarm() {
        log.info("Remind alarm triggered");

        List<Member> targetMembers = alarmService.findMembersForRemindAlarm(LocalDate.now());

        fireBaseCloudMessageService.sendRemindAlarm(targetMembers);
    }
}
