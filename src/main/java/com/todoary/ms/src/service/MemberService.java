package com.todoary.ms.src.service;

import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Provider;
import com.todoary.ms.src.domain.ProviderAccount;
import com.todoary.ms.src.domain.token.RefreshToken;
import com.todoary.ms.src.exception.common.TodoaryException;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.web.dto.MemberJoinParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.todoary.ms.util.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long join(MemberJoinParam memberJoinParam) {
        Member newMember = Member.builder()
                .name(memberJoinParam.getName())
                .nickname(memberJoinParam.getNickname())
                .email(memberJoinParam.getEmail())
                .password(memberJoinParam.getPassword())
                .role(memberJoinParam.getRole())
                .providerAccount(new ProviderAccount(Provider.NONE, "none"))
                .isTermsEnable(memberJoinParam.isTermsEnable())
                .build();
        return memberRepository.save(newMember);
    }

    public Boolean existsById(Long memberId) {
        return memberRepository.existById(memberId);
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new TodoaryException(USERS_DELETED_USER));
    }

    public Boolean existsByRefreshToken(RefreshToken refreshToken) {
        return memberRepository.existByRefreshToken(refreshToken);
    }


    public void validateMemberByRefreshToken(String refreshTokenCode) {
        Long memberId = Long.parseLong(jwtTokenProvider.getUserIdFromRefreshToken(refreshTokenCode));
        Member findMember = findById(memberId);

        if (!findMember.hasRefreshTokenCode(refreshTokenCode)) {
            throw new TodoaryException(INVALID_JWT);
        }
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(
                () -> new TodoaryException(USERS_EMPTY_USER_EMAIL));
    }

    public Member findByProviderEmail(String email, String providerName) {
        return memberRepository.findByProviderEmail(email, providerName).orElseThrow(
                () -> new TodoaryException(USERS_EMPTY_USER_EMAIL));
    }

    public void checkEmailDuplication(String email) {
        if (memberRepository.isProviderEmailUsed(Provider.NONE, email)) {
            throw new TodoaryException(POST_USERS_EXISTS_EMAIL);
        }
    }

    public void changePassword(String email, String newPassword) {
        Member member = findByEmail(email);
        member.changePassword(encodePassword(newPassword));
    }
}
