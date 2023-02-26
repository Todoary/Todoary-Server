package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.alarm.FireBaseCloudMessageService;
import com.todoary.ms.src.alarm.model.Alarm;
import com.todoary.ms.src.common.util.ErrorLogWriter;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Todo;
import com.todoary.ms.src.service.AlarmService;
import com.todoary.ms.src.service.MemberService;
import com.todoary.ms.src.service.todo.JpaTodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/jpa/alarm")
public class JpaAlarmController {
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
