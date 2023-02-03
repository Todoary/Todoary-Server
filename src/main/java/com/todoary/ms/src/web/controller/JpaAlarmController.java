package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.alarm.FireBaseCloudMessageService;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
