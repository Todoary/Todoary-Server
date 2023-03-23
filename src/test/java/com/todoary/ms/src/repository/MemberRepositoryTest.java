package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.ProviderAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.todoary.ms.src.domain.ProviderAccount.googleFrom;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Value("${profile-image.default-url}")
    private String defaultProfileImageUrl;

    @Test
    public void 멤버를_이메일과_provider로_검색_유저_존재O() throws Exception {
        //given
        ProviderAccount providerAccount = createProviderAccount();
        Member member = Member.builder()
                .name("memberA")
                .email("member@member")
                .providerAccount(providerAccount)
                .isTermsEnable(true)
                .build();

        memberRepository.save(member);

        //when
        Boolean result = memberRepository.isEmailOfGeneralMemberUsed("member@member");

        //then
        assertThat(result).isTrue();
    }

    @Test
    public void 멤버를_이메일과_provider로_검색_유저_존재X() throws Exception {
        //given
        ProviderAccount providerAccount = createProviderAccount();
        Member member = Member.builder()
                .name("memberA")
                .email("member@member")
                .providerAccount(providerAccount)
                .isTermsEnable(true)
                .build();

        memberRepository.save(member);

        //when
        Boolean result = memberRepository.isEmailOfGeneralMemberUsed("member2@member");

        //then
        assertThat(result).isFalse();
    }

    /**
     * checkNickname() -> isNicknameUsed()
     */

    @Test
    public void 멤버를_닉네임으로_검색_유저_존재O() throws Exception {
        //given
        Member member = createMember();
        em.persist(member);

        //when
        Boolean result = memberRepository.isNicknameUsed("memberNickname");

        //then
        assertThat(result).isTrue();
    }

    @Test
    public void 멤버를_닉네임으로_검색_유저_존재X() throws Exception {
        //given
        Member member = createMember();

        //when
        Boolean result = memberRepository.isNicknameUsed("otherMemberNickname");

        //then
        assertThat(result).isFalse();
    }

    @Test
    public void 구글로_가입된_멤버를_providerId로_검색_유저_존재O() throws Exception {
        //given
        ProviderAccount providerAccount = createProviderAccount();
        Member member = createMember(providerAccount);
        em.persist(member);

        //when
        Boolean result = memberRepository.isActiveByProviderAccount(providerAccount);

        //then
        assertThat(result).isTrue();
    }

    @Test
    public void 구글로_가입된_멤버를_providerId로_검색_유저_존재X() throws Exception {
        //given
        ProviderAccount providerAccount = createProviderAccount();
        ProviderAccount providerAccountNonExists = googleFrom("google2");

        Member member = createMember(providerAccount);
        em.persist(member);

        //when
        Boolean result = memberRepository.isActiveByProviderAccount(providerAccountNonExists);

        //then
        assertThat(result).isFalse();
    }

    @Test
    public void status가_0이_된지_30일_된_멤버_삭제() throws Exception {
        //given
        Member member = createMember();
        setInActive(member);
        em.persist(member);

        // 멤버 delete를 위해 영속성 컨텍스를 비움
        em.flush();
        em.clear();

        // 현재부로 30일 전의 시간
        LocalDateTime deletedDateTime = LocalDateTime.of(
                LocalDate.now().minusDays(30),
                LocalTime.of(0,0,0)
        );

        //when

        // 멤버의 modifiedAt을 30일 전으로 변경
        em.createQuery("update Member m " +
                        "set m.modifiedAt = :deletedDateTime " +
                        "where m.id = :memberId")
                .setParameter("deletedDateTime", deletedDateTime)
                .setParameter("memberId", member.getId())
                .executeUpdate();
        memberRepository.deleteByStatus();

        //then

        Member findMember = em.find(Member.class, member.getId());
        assertThat(findMember).isNull();
    }

    void setInActive(Member member) throws NoSuchFieldException, IllegalAccessException {
        Class<? extends Member> memberClass = member.getClass();

        // field, status 수정 (0)
        Field status = memberClass.getDeclaredField("status");
        status.setAccessible(true);
        status.set(member, 0);
    }

    Member createMember() {
        return Member.builder()
                .name("member")
                .nickname("memberNickname")
                .email("memberEmail")
                .password("123456")
                .isTermsEnable(true)
                .build();
    }

    Member createMember(ProviderAccount providerAccount) {
        return Member.builder()
                .name("member")
                .nickname("memberNickname")
                .email("memberEmail")
                .password("123456")
                .providerAccount(providerAccount)
                .isTermsEnable(true)
                .build();
    }

    ProviderAccount createProviderAccount() {
        ProviderAccount providerAccount = googleFrom("google1");
        return providerAccount;
    }
}