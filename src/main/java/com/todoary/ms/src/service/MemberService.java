package com.todoary.ms.src.service;

import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.token.RefreshToken;
import com.todoary.ms.src.exception.common.TodoaryException;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Long join(Member newMember) {
        return memberRepository.save(newMember);
    }

    public Boolean existsById(Long memberId) {
        return memberRepository.existById(memberId);
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new TodoaryException(BaseResponseStatus.USERS_DELETED_USER));
    }

    public Boolean existsByRefreshToken(RefreshToken refreshToken) {
        return memberRepository.existByRefreshToken(refreshToken);
    }


    public void validateMemberByRefreshToken(String refreshTokenCode) {
        Long memberId = Long.parseLong(jwtTokenProvider.getUserIdFromRefreshToken(refreshTokenCode));
        Member findMember = findById(memberId);

        if (!findMember.hasRefreshTokenCode(refreshTokenCode)) {
            throw new TodoaryException(BaseResponseStatus.INVALID_JWT);
        }
    }
}
