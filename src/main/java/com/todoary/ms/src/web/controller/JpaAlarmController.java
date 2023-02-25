package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.alarm.FireBaseCloudMessageService;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Todo;
import com.todoary.ms.src.service.MemberService;
import com.todoary.ms.src.service.todo.JpaTodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/jpa/alarm")
public class JpaAlarmController {
    private final MemberService memberService;
    private final FireBaseCloudMessageService fireBaseCloudMessageService;

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void pushDailyAlarm(){
        List<Member> dailyAlarmEnabledMembers = memberService.findAllDailyAlarmEnabled();

        dailyAlarmEnabledMembers.stream()
                        .map(member -> member.getFcmToken().getCode())
                        .forEach(fcmToken -> fireBaseCloudMessageService.sendMessageTo(
                                fcmToken,
                                "하루기록 알림",
                                "하루기록을 작성해보세요.")
                        );
    }

    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void pushTodoAlarm() {
        LocalTime now = LocalTime.now();
        LocalTime targetTime = LocalTime.of(now.getHour(), now.getMinute());

        fireBaseCloudMessageService.sendTodoAlarm(LocalDate.now(), targetTime);

    }
}
