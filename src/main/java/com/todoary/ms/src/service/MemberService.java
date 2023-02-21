package com.todoary.ms.src.service;

import com.todoary.ms.src.common.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.domain.*;
import com.todoary.ms.src.domain.token.RefreshToken;
import com.todoary.ms.src.common.exception.TodoaryException;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

import static com.todoary.ms.src.common.response.BaseResponseStatus.*;
import static com.todoary.ms.src.common.util.ColumnLengthInfo.MEMBER_NICKNAME_MAX_LENGTH;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    @Value("${profile-image.default-url}")
    private String defaultProfileImageUrl;

    @Transactional
    public Long joinGeneralMember(MemberJoinParam generalMemberJoinParam) {
        return join(generalMemberJoinParam, ProviderAccount.none());
    }

    @Transactional
    public Long joinOauthMember(OauthMemberJoinParam oauthMemberJoinParam) {
        MemberJoinParam joinParam = MemberJoinParam.builder()
                .name(oauthMemberJoinParam.getName())
                .email(oauthMemberJoinParam.getEmail())
                .role(oauthMemberJoinParam.getRole())
                .isTermsEnable(oauthMemberJoinParam.isTermsEnable())
                .nickname(generateRandomNickname())
                .password(passwordEncoder.encode(oauthMemberJoinParam.getEmail()))
                .build();
        return join(joinParam, oauthMemberJoinParam.getProviderAccount());
    }

    private Long join(MemberJoinParam memberJoinParam, ProviderAccount provider) {
        Member newMember = Member.builder()
                .name(memberJoinParam.getName())
                .nickname(memberJoinParam.getNickname())
                .email(memberJoinParam.getEmail())
                .password(memberJoinParam.getPassword())
                .role(memberJoinParam.getRole())
                .isTermsEnable(memberJoinParam.isTermsEnable())
                .providerAccount(provider)
                .profileImgUrl(defaultProfileImageUrl)
                .build();
        init(newMember);
        return memberRepository.save(newMember);
    }

    private void init(Member newMember) {
        Category.createInitialCategoryOf(newMember);
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

    public List<Member> findAllDailyAlarmEnabled() {
        return memberRepository.findAllDailyAlarmEnabled();
    }

    @Transactional
    public void updateProfile(Long memberId, MemberProfileRequest request) {
        Member member = findById(memberId);
        member.update(
                request.getNickname(),
                request.getIntroduce()
        );
    }

    @Transactional
    public MemberResponse findMemberProfile(Long memberId) {
        return MemberResponse.from(findById(memberId));
    }

    @Transactional
    public void activeTodoAlarm(Long memberId, boolean toDoAlarmEnable) {
        Member member = findById(memberId);
        member.activeTodoAlarm(toDoAlarmEnable);
    }

    @Transactional
    public void activeDailyAlarm(Long memberId, boolean dailyAlarmEnable) {
        Member member = findById(memberId);
        member.activeDailyAlarm(dailyAlarmEnable);
    }

    @Transactional
    public void activeRemindAlarm(Long memberId, boolean remindAlarmEnable) {
        Member member = findById(memberId);
        member.activeRemindAlarm(remindAlarmEnable);
    }

    @Transactional
    public void activeTermsStatus(Long memberId, boolean isTermsEnable) {
        Member member = findById(memberId);
        member.activeTermsStatus(isTermsEnable);
    }

    @Transactional
    public void removeMember(Long memberId) {
        memberRepository.updateStatus(memberId);
    }

    @Transactional
    public void changeProfileImg(Long memberId, String newProfileImgUrl) {
        Member member = findById(memberId);

        member.changeProfileImg(newProfileImgUrl);
    }

    @Transactional
    public void removeTokens(Long memberId) {
        Member member = findById(memberId);

        member.removeRefreshToken();
        member.removeFcmToken();
    }

    public String getProfileImgUrlById(Long memberId) {
        return findById(memberId)
                .getProfileImgUrl();
    }

    public boolean existsByProviderAccount(ProviderAccount providerAccount) {
        return memberRepository.isActiveByProviderAccount(providerAccount);
    }

    private String generateRandomNickname() {
        // 아스키 코드 48 ~ 122까지 랜덤 문자
        // 예: qOji6mPStx
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        String nickname = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)) // 아스키코드 숫자 알파벳 중간에 섞여있는 문자들 제거
                .limit(MEMBER_NICKNAME_MAX_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return nickname;
    }
}
