package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.service.alarm.FireBaseCloudMessageService;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.service.alarm.AlarmService;
import com.todoary.ms.src.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class AlarmController {
    private final MemberService memberService;
    private final AlarmService alarmService;
    private final FireBaseCloudMessageService fireBaseCloudMessageService;

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void pushDailyAlarm(){
        List<Member> dailyAlarmEnabledMembers = memberService.findAllDailyAlarmEnabled();

        fireBaseCloudMessageService.sendDailyAlarm(dailyAlarmEnabledMembers);
    }

    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void pushTodoAlarm() {
        LocalTime now = LocalTime.now();
        LocalTime targetTime = LocalTime.of(now.getHour(), now.getMinute());

        fireBaseCloudMessageService.sendTodoAlarm(LocalDate.now(), targetTime);
    }

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void pushRemindAlarm() {
        List<Member> targetMembers = alarmService.findMembersForRemindAlarm(LocalDate.now());

        fireBaseCloudMessageService.sendRemindAlarm(targetMembers);
    }
}
