package com.todoary.ms.src.service;

import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.RemindAlarm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AlarmService {
    private final MemberService memberService;
    
    @Transactional
    public void updateRemindAlarmToDate(Long memberId, LocalDate alarmDate) {
        Member member = memberService.findById(memberId);
        member.changeRemindAlarm(new RemindAlarm(member, alarmDate));
    }

    public List<Member> findMembersForRemindAlarm(LocalDate now) {
        return memberService.findAllForRemindAlarm(now);
    }
}
