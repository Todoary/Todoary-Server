package com.todoary.ms.src.service;

import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.token.RefreshToken;
import com.todoary.ms.src.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Long join(Member newMember) {
        return memberRepository.save(newMember);
    }

    public Boolean existsById(Long memberId) {
        return memberRepository.existById(memberId);
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow();
    }

    public Boolean existsByRefreshToken(RefreshToken refreshToken) {
        return memberRepository.existByRefreshToken(refreshToken);
    }
}
