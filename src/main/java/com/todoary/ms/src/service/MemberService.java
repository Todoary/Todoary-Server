package com.todoary.ms.src.service;

import com.todoary.ms.src.common.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.common.exception.TodoaryException;
import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Provider;
import com.todoary.ms.src.domain.ProviderAccount;
import com.todoary.ms.src.domain.token.FcmToken;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.s3.AwsS3Service;
import com.todoary.ms.src.web.dto.MemberJoinParam;
import com.todoary.ms.src.web.dto.MemberProfileParam;
import com.todoary.ms.src.web.dto.MemberResponse;
import com.todoary.ms.src.web.dto.OauthMemberJoinParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.todoary.ms.src.common.response.BaseResponseStatus.*;
import static com.todoary.ms.src.common.util.ColumnLengthInfo.MEMBER_NICKNAME_MAX_LENGTH;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AwsS3Service awsS3Service;

    private final PasswordEncoder passwordEncoder;

    @Value("${profile-image.default-url}")
    private String defaultProfileImageUrl;

    @Transactional
    public Long joinGeneralMember(MemberJoinParam generalMemberJoinParam) {
        removeGeneralMemberIfDeactivatedBefore(generalMemberJoinParam.getEmail());
        return join(generalMemberJoinParam, ProviderAccount.none());
    }

    @Transactional
    public Long joinOauthMember(OauthMemberJoinParam oauthMemberJoinParam) {
        removeMemberIfDeactivatedBefore(oauthMemberJoinParam.getEmail(), oauthMemberJoinParam.getProviderAccount());
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

    private void removeGeneralMemberIfDeactivatedBefore(String email) {
        removeMemberIfDeactivatedBefore(email, ProviderAccount.none());
    }

    private void removeMemberIfDeactivatedBefore(String email, ProviderAccount providerAccount) {
        memberRepository.findByEmailAndProviderAccount(email, providerAccount)
                .filter(Member::isDeactivated)
                .ifPresent(member -> {
                    log.info("탈퇴한 멤버 재가입 | 이메일: {} / Provider: {} / 탈퇴날짜: {}", email, providerAccount.getProvider(), member.getModifiedAt());
                    memberRepository.removeMember(member);
                });
    }

    private Long join(MemberJoinParam memberJoinParam, ProviderAccount provider) {
        checkNicknameNotUsed(memberJoinParam.getNickname());
        checkEmailAndOAuthAccountNotUsed(memberJoinParam.getEmail(), provider);
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

    @Transactional(readOnly = true)
    public Member findById(Long memberId) {
        return checkMemberValid(memberRepository.findById(memberId));
    }

    private Member checkMemberValid(Optional<Member> memberOrNull) {
        return checkMemberNotDeactivated(checkMemberExists(memberOrNull));
    }

    private Member checkMemberExists(Optional<Member> memberOrNull) {
        return memberOrNull
                .orElseThrow(() -> new TodoaryException(EMPTY_USER));
    }

    private Member checkMemberNotDeactivated(Member member) {
        if (member.isDeactivated()) {
            throw new TodoaryException(USERS_DELETED_USER);
        }
        return member;
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public Member findActiveGeneralMemberByEmail(String email) {
        return checkMemberValid(memberRepository.findGeneralMemberByEmail(email));
    }

    @Transactional(readOnly = true)
    public Member findActiveMemberByProviderEmail(Provider provider, String email) {
        return checkMemberValid(memberRepository.findByProviderEmail(provider, email));
    }

    @Transactional(readOnly = true)
    public Optional<Member> findMemberOrEmptyByProviderAccount(ProviderAccount providerAccount) {
        return memberRepository.findByProviderAccount(providerAccount);
    }

    @Transactional(readOnly = true)
    public Member findActiveMemberByProviderAccount(ProviderAccount providerAccount) {
        return checkMemberValid(memberRepository.findByProviderAccount(providerAccount));
    }

    @Transactional(readOnly = true)
    public Member findActiveMemberByEmailAndProviderAccount(String email, ProviderAccount providerAccount) {
        return checkMemberValid(memberRepository.findByEmailAndProviderAccount(email, providerAccount));
    }

    @Transactional(readOnly = true)
    public Member findMemberByEmailAndProviderAccount(String email, ProviderAccount providerAccount) {
        return checkMemberExists(memberRepository.findByEmailAndProviderAccount(email, providerAccount));
    }

    private void checkEmailAndOAuthAccountNotUsed(String email, ProviderAccount providerAccount) {
        if (memberRepository.isProviderAccountAndEmailUsed(providerAccount, email)) {
            throw new TodoaryException(MEMBERS_DUPLICATE_EMAIL);
        }
    }

    @Transactional(readOnly = true)
    public void checkEmailDuplicationOfGeneral(String email) {
        checkEmailAndOAuthAccountNotUsed(email, ProviderAccount.none());
    }

    @Transactional
    public void changePassword(String email, String newPassword) {
        Member member = findActiveGeneralMemberByEmail(email);
        member.changePassword(encodePassword(newPassword));
    }

    @Transactional(readOnly = true)
    public List<Member> findAllDailyAlarmEnabled() {
        return memberRepository.findAllDailyAlarmEnabled();
    }

    @Transactional
    public void updateProfile(Long memberId, MemberProfileParam param) {
        Member member = findById(memberId);
        if (!member.getNickname().equals(param.getNickname())) {
            checkNicknameNotUsed(param.getNickname());
        }
        member.update(
                param.getNickname(),
                param.getIntroduce()
        );
    }

    private void checkNicknameNotUsed(String nickname) {
        if (memberRepository.isNicknameUsed(nickname)) {
            throw new TodoaryException(MEMBERS_DUPLICATE_NICKNAME);
        }
    }

    @Transactional(readOnly = true)
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
    public void deactivateMember(Long memberId) {
        Member member = findById(memberId);
        deactivateMember(member);
    }

    @Transactional
    public void deactivateMember(Member member) {
        member.deactivate();
    }

    @Transactional
    public void removeMember(Member member) {
        memberRepository.removeMember(member);
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

    @Transactional(readOnly = true)
    public String getProfileImgUrlById(Long memberId) {
        return findById(memberId)
                .getProfileImgUrl();
    }

    @Transactional(readOnly = true)
    public boolean existsActiveMemberByProviderAccount(ProviderAccount providerAccount) {
        return memberRepository.isActiveByProviderAccount(providerAccount);
    }

    @Transactional(readOnly = true)
    public List<Member> findAllForRemindAlarm(LocalDate targetDate) {
        return memberRepository.findAllForRemindAlarm(targetDate);
    }

    @Transactional(readOnly = true)
    public boolean existsByGeneralEmail(String email) {
        return memberRepository.findGeneralMemberByEmail(email)
                .isPresent();
    }

    @Transactional(readOnly = true)
    public boolean existsDeactivatedGeneralMemberByEmail(String email) {
        return memberRepository.findGeneralMemberByEmail(email)
                .map(Member::isDeactivated)
                .orElse(false);
    }

    @Transactional
    public void modifyFcmToken(Long memberId, String newFcmToken) {
        Member member = findById(memberId);

        if (member.getFcmToken() == null) {
            new FcmToken(member, newFcmToken);
            return;
        }
        member.updateFcmToken(newFcmToken);
    }

    @Transactional(readOnly = true)
    public boolean checkProfileImgIsDefault(Long memberId) {
        return findById(memberId)
                .getProfileImgUrl()
                .equals(defaultProfileImageUrl);
    }

    @Transactional
    public void setProfileImgDefault(Long memberId) {
        Member member = findById(memberId);
        String profileImgUrl = member.getProfileImgUrl();

        if (profileImgUrl.equals(defaultProfileImageUrl)) {
            return;
        }

        // delete image at S3 storage
        awsS3Service.fileDelete(member.getProfileImgUrl());
        // set profile-image default
        member.changeProfileImg(defaultProfileImageUrl);
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

    @Transactional(readOnly = true)
    public boolean isEmailUsedByDeactivatedGeneralMember(String email) {
        return memberRepository.findGeneralMemberByEmail(email)
                .map(Member::isDeactivated)
                .orElse(false);
    }

    @Transactional
    public void activateMember(String email, ProviderAccount account) {
        Member member = findMemberByEmailAndProviderAccount(email, account);
        member.activate();
    }
}
