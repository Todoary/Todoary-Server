package com.todoary.ms.src.service;


import com.todoary.ms.src.common.exception.TodoaryException;
import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.ProviderAccount;
import com.todoary.ms.src.repository.FcmTokenRepository;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.repository.RefreshTokenRepository;
import com.todoary.ms.src.s3.AwsS3Service;
import com.todoary.ms.src.web.dto.MemberJoinParam;
import com.todoary.ms.src.web.dto.OauthMemberJoinParam;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.todoary.ms.src.common.response.BaseResponseStatus.*;
import static com.todoary.ms.src.domain.Category.InitialCategoryValue.initialColor;
import static com.todoary.ms.src.domain.Category.InitialCategoryValue.initialTitle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class MemberServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    FcmTokenRepository fcmTokenRepository;
    @MockBean
    AwsS3Service awsS3Service;
    @Value("${profile-image.default-url}")
    private String defaultProfileImageUrl;

    @Test
    public void 일반_멤버_회원가입_O() throws Exception {
        //given
        MemberJoinParam memberJoinParam = createMemberJoinParam();
        //when
        Long joinMemberId = memberService.joinGeneralMember(memberJoinParam);
        Member member = memberService.findById(joinMemberId);
        //then
        assertThat(member.getEmail()).isEqualTo(memberJoinParam.getEmail());
        assertThat(member.getProviderAccount()).isEqualTo(ProviderAccount.none());
    }

    @Test
    void 같은_닉네임_일반멤버_회원가입_X() {
        // given
        String nickname = "nickname";
        memberService.joinGeneralMember(createMemberJoinParam(nickname, "email1"));
        // when
        MemberJoinParam memberJoinParam = createMemberJoinParam(nickname, "email2");
        ThrowingCallable action = () -> memberService.joinGeneralMember(memberJoinParam);
        // then
        assertThatThrownBy(action)
                .isInstanceOf(TodoaryException.class)
                .matches(e -> ((TodoaryException) e).getStatus().equals(MEMBERS_DUPLICATE_NICKNAME));
    }

    @Test
    void 같은_이메일_일반멤버_회원가입_X() {
        // given
        String email = "email";
        memberService.joinGeneralMember(createMemberJoinParam("nickname1", email));
        // when
        MemberJoinParam memberJoinParam = createMemberJoinParam("nickname2", email);
        ThrowingCallable action = () -> memberService.joinGeneralMember(memberJoinParam);
        // then
        assertThatThrownBy(action)
                .isInstanceOf(TodoaryException.class)
                .matches(e -> ((TodoaryException) e).getStatus().equals(MEMBERS_DUPLICATE_EMAIL));
    }

    @Test
    void 같은_이메일_일반멤버_탈퇴했을때_재가입_O() {
        // given
        Long memberId = memberService.joinGeneralMember(createMemberJoinParam("nickname1", "email@email.com"));
        memberService.findById(memberId).deactivate();
        // when
        MemberJoinParam memberJoinParam = createMemberJoinParam("nickname2", "email@email.com");
        Long newMemberId = memberService.joinGeneralMember(memberJoinParam);
        Member newMember = memberService.findById(newMemberId);
        // then
        assertThat(memberId).isNotEqualTo(newMemberId);
        assertThat(newMember.getNickname()).isEqualTo("nickname2");
    }

    @Test
    void 같은_이메일_애플멤버_탈퇴했을때_재가입_O() {
        // given
        Long memberId = memberService.joinOauthMember(createOauthMemberJoinParamOfEmail("email@email.com"));
        memberService.findById(memberId).deactivate();
        // when
        Long newMemberId = memberService.joinOauthMember(createOauthMemberJoinParamOfEmail("email@email.com"));
        Member newMember = memberService.findById(newMemberId);
        // then
        assertThat(memberId).isNotEqualTo(newMemberId);
        assertThat(newMember.getEmail()).isEqualTo("email@email.com");
        assertThatThrownBy(() -> memberService.findById(memberId))
                .isInstanceOf(TodoaryException.class)
                .matches(e -> ((TodoaryException) e).getStatus().equals(EMPTY_USER));
    }

    @Test
    void 존재하는_일반멤버_이메일로_조회O() {
        // given
        String email = "email";
        MemberJoinParam memberJoinParam = createMemberJoinParamOfEmail(email);
        memberService.joinGeneralMember(memberJoinParam);
        // when
        Member member = memberService.findActiveGeneralMemberByEmail(email);
        // then
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(ProviderAccount.none()).isEqualTo(ProviderAccount.none());
    }

    @Test
    void 탈퇴안한_존재하는_일반멤버_조회O() {
        // given
        memberService.joinGeneralMember(createMemberJoinParamOfEmail("email@email.com"));
        // when
        boolean exists = memberService.existsByGeneralEmail("email@email.com");
        // then
        assertThat(exists).isTrue();
    }

    @Test
    void 탈퇴한_존재하는_일반멤버_조회O() {
        // given
        Long memberId = memberService.joinGeneralMember(createMemberJoinParamOfEmail("email@email.com"));
        memberService.deactivateMember(memberId);
        // when
        boolean exists = memberService.existsByGeneralEmail("email@email.com");
        // then
        assertThat(exists).isTrue();
    }

    @Test
    void 존재하지않는_일반멤버_조회X() {
        // given
        memberService.joinOauthMember(createOauthMemberJoinParamOfEmail("email@email.com"));
        // when
        boolean exists = memberService.existsByGeneralEmail("email@email.com");
        // then
        assertThat(exists).isFalse();
    }

    @Test
    void 가입한_일반멤버_id_조회O() {
        // given
        MemberJoinParam memberJoinParam = createMemberJoinParam();
        Long joinMemberId = memberService.joinGeneralMember(memberJoinParam);
        // when
        Member member = memberService.findById(joinMemberId);
        // then
        assertThat(member.getId()).isEqualTo(joinMemberId);
        assertThat(member.isDeactivated()).isFalse();
    }

    @Test
    void 멤버_조회시_id없을때_exception() {
        // given
        // when
        ThrowingCallable action = () -> memberService.findById(10L);
        // then
        assertThatThrownBy(action)
                .isInstanceOf(TodoaryException.class)
                .matches(e -> ((TodoaryException) e).getStatus().equals(EMPTY_USER));
    }

    @Test
    void 멤버_회원가입시_기본_카테고리_생성() {
        // given
        MemberJoinParam memberJoinParam = createMemberJoinParam();
        // when
        Long joinMemberId = memberService.joinGeneralMember(memberJoinParam);
        // then
        List<Category> categories = memberService.findById(joinMemberId).getCategories();
        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getTitle()).isEqualTo(initialTitle);
        assertThat(categories.get(0).getColor()).isEqualTo(initialColor);
        assertThat(categories.get(0).getMember().getId()).isEqualTo(joinMemberId);
    }

    @Test
    void 멤버_생성시_기본이미지_있어야함O() {
        // given
        MemberJoinParam memberJoinParam = createMemberJoinParam();
        // when
        Long joinMemberId = memberService.joinGeneralMember(memberJoinParam);
        String profileImgUrl = memberService.findById(joinMemberId).getProfileImgUrl();
        // then
        assertThat(profileImgUrl).isEqualTo(defaultProfileImageUrl);
    }

    @Test
    void 멤버_조회시_id있지만_탈퇴한멤버_exception() {
        // given
        MemberJoinParam memberJoinParam = createMemberJoinParam();
        Long joinMemberId = memberService.joinGeneralMember(memberJoinParam);
        // when
        memberService.deactivateMember(joinMemberId);
        // then
        assertThatThrownBy(() -> memberService.findById(joinMemberId))
                .isInstanceOf(TodoaryException.class)
                .matches(e -> ((TodoaryException) e).getStatus().equals(USERS_DELETED_USER));
    }

    @Test
    void 멤버_탈퇴시_deleted_True() {
        // given
        MemberJoinParam memberJoinParam = createMemberJoinParam();
        Long joinMemberId = memberService.joinGeneralMember(memberJoinParam);
        // when
        memberService.deactivateMember(joinMemberId);
        Member member = memberRepository.findById(joinMemberId).get();
        // then
        assertThat(member.isDeactivated()).isTrue();
    }

    @Test
    void 멤버_탈퇴시_리프레쉬_FCM_토큰삭제() {
        // given
        MemberJoinParam memberJoinParam = createMemberJoinParam();
        Long joinMemberId = memberService.joinGeneralMember(memberJoinParam);
        // when
        memberService.deactivateMember(joinMemberId);
        Member member = memberRepository.findById(joinMemberId).get();
        // then
        assertThat(member.getRefreshToken()).isNull();
        assertThat(member.getFcmToken()).isNull();
        assertThat(refreshTokenRepository.findByMemberId(joinMemberId)).isEmpty();
    }

    @Test
    void 소셜멤버_동등성_검사_provider기준() {
        // given
        Member member1 = Member.builder().email("email1").providerAccount(ProviderAccount.appleFrom("providerId")).build();
        Member member2 = Member.builder().email("email2").providerAccount(ProviderAccount.appleFrom("providerId")).build();
        // when
        // then
        assertThat(member1).isEqualTo(member2);
    }

    @Test
    void 일반멤버_동등성_검사_email기준() {
        // given
        Member member1 = Member.builder().email("email").providerAccount(ProviderAccount.none()).build();
        Member member2 = Member.builder().email("email").providerAccount(ProviderAccount.none()).build();
        // when
        // then
        assertThat(member1).isEqualTo(member2);
    }

    @Test
    void Provider_none_동등성_검사() {
        // given
        // when
        ProviderAccount none1 = ProviderAccount.none();
        ProviderAccount none2 = ProviderAccount.none();
        // then
        assertThat(none1).isEqualTo(none2);
    }

    @Test
    void Provider_apple_동등성_검사() {
        // given
        String providerId = "1234";
        // when
        ProviderAccount apple1 = ProviderAccount.appleFrom(providerId);
        ProviderAccount apple2 = ProviderAccount.appleFrom(providerId);
        // then
        assertThat(apple1).isEqualTo(apple2);
    }

    @Test
    public void 프로필사진이_default일때_check() throws Exception {
        //given
        Long memberId = memberService.joinGeneralMember(createMemberJoinParam());

        //when
        boolean result = memberService.checkProfileImgIsDefault(memberId);

        //then
        assertThat(result).isTrue();
    }

    @Test
    public void 프로필사진이_default가_아닐때_check() throws Exception {
        //given
        Member member = Member.builder()
                .email("emailA")
                .profileImgUrl("modifiedProfileImgUrl")
                .providerAccount(ProviderAccount.none())
                .build();
        em.persist(member);

        //when
        boolean result = memberService.checkProfileImgIsDefault(member.getId());

        //then
        assertThat(result).isFalse();
    }

    @Test
    public void 프로필_사진_초기화테스트_기본_프로필사진일때() throws Exception {
        //given
        Long memberId = memberService.joinGeneralMember(createMemberJoinParam());

        //when
        memberService.setProfileImgDefault(memberId);

        //then
        assertThat(memberService.findById(memberId).getProfileImgUrl()).isEqualTo(defaultProfileImageUrl);
    }

    @Test
    public void 프로필_사진_초기화테스트_수정된_프로필사진일때() throws Exception {
        //given
        when(awsS3Service.fileDelete(any())).thenReturn(true);

        Member member = Member.builder()
                .email("emailA")
                .profileImgUrl("modifiedProfileImgUrl")
                .providerAccount(ProviderAccount.none())
                .build();

        em.persist(member);
        //when
        memberService.setProfileImgDefault(member.getId());

        //then
        Member findMember = memberService.findById(member.getId());
        assertThat(findMember.getProfileImgUrl()).isEqualTo(defaultProfileImageUrl);
    }

    @Test
    void 존재하지_않는_멤버의_탈퇴여부_조회_false() {
        // given
        // when
        boolean isUsed = memberService.isEmailUsedByDeactivatedGeneralMember("email@email.com");
        // then
        assertThat(isUsed).isFalse();
    }

    @Test
    void 탈퇴하지_않은_멤버의_탈퇴여부_조회_false() {
        // given
        memberService.joinGeneralMember(createMemberJoinParamOfEmail("email@email.com"));
        // when
        boolean isUsed = memberService.isEmailUsedByDeactivatedGeneralMember("email@email.com");
        // then
        assertThat(isUsed).isFalse();
    }

    @Test
    void 탈퇴한_멤버의_탈퇴여부_조회_true() {
        // given
        Long memberId = memberService.joinGeneralMember(createMemberJoinParamOfEmail("email@email.com"));
        memberService.deactivateMember(memberId);
        // when
        boolean isUsed = memberService.isEmailUsedByDeactivatedGeneralMember("email@email.com");
        // then
        assertThat(isUsed).isTrue();
    }

    static MemberJoinParam createMemberJoinParam(String nickname, String email) {
        return new MemberJoinParam("memberA",
                                   nickname,
                                   email,
                                   "passwordA",
                                   "ROLE_USER",
                                   true);
    }

    static MemberJoinParam createMemberJoinParamOfEmail(String email) {
        return createMemberJoinParam("nicknameA", email);
    }

    static MemberJoinParam createMemberJoinParamOfNickname(String nickname) {
        return createMemberJoinParam(nickname, "emailA");
    }

    static MemberJoinParam createMemberJoinParam() {
        return createMemberJoinParam("nicknameA", "emailA");
    }

    static OauthMemberJoinParam createOauthMemberJoinParamOfEmail(String email) {
        return new OauthMemberJoinParam("name", email, ProviderAccount.appleFrom("1234"), "USER_ROLE", true);
    }
}
