package com.todoary.ms.src.service.alarm;

import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.RemindAlarm;
import com.todoary.ms.src.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AlarmService {
    private final MemberService memberService;
    
    @Transactional
    public void updateRemindAlarmToDate(Long memberId, LocalDate alarmDate) {
        Member member = memberService.findActiveMemberById(memberId);
        member.changeRemindAlarm(new RemindAlarm(member, alarmDate));
    }

    public List<Member> findMembersForRemindAlarm(LocalDate now) {
        return memberService.findAllForRemindAlarm(now);
    }
}
