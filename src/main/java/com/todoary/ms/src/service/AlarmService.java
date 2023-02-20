package com.todoary.ms.src.service;

import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.RemindAlarm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class AlarmService {
    private final MemberService memberService;
    
    @Transactional
    public void updateRemindAlarmToDate(Long memberId, LocalDate alarmDate) {
        Member member = memberService.findById(memberId);
        member.changeRemindAlarm(new RemindAlarm(member, alarmDate));
    }
}
