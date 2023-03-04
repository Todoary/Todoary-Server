package com.todoary.ms.src.service;

import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExpiredMemberHandlerService {
    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public int deleteMembersElapsed30DaysAfterDeactivated() {
        log.info("탈퇴한 지 한달 지난 멤버 삭제 확인 triggered");
        LocalDateTime before30Days = LocalDateTime.now().minusDays(30);
        return deleteMembersDeactivatedTimeBefore(before30Days);
    }

    @Transactional
    public int deleteMembersDeactivatedTimeBefore(LocalDateTime time){
        List<Member> membersShouldBeDeleted = memberRepository.findMemberDeactivatedTimeBefore(time);
        if (!membersShouldBeDeleted.isEmpty()){
            membersShouldBeDeleted.forEach(memberRepository::removeMember);
            log.info("{}명의 탈퇴한 멤버 삭제됨", membersShouldBeDeleted.size());
        }
        return membersShouldBeDeleted.size();
    }
}
