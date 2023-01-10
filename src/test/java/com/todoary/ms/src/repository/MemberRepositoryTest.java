package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Provider;
import com.todoary.ms.src.domain.ProviderAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

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
        Boolean result = memberRepository.isProviderEmailUsed(Provider.GOOGLE, "member@member");

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
        Boolean result = memberRepository.isProviderEmailUsed(Provider.GOOGLE, "member2@member");

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
    public void 닉네임을_다른_멤버가_사용중O() throws Exception {
        //given
        Member member1 = createMember();
        em.persist(member1);

        Member member2 = createMember();
        em.persist(member2);

        //when
        Boolean result = memberRepository.isNicknameUsedByOthers(member1.getId(), member1.getNickname());

        //then
        assertThat(result).isTrue();
    }

    @Test
    public void 닉네임을_다른_멤버가_사용중X() throws Exception {
        //given
        Member member1 = createMember();
        em.persist(member1);

        Member member2 = Member.builder().nickname("member2").build();
        em.persist(member2);

        //when
        Boolean result = memberRepository.isNicknameUsedByOthers(member1.getId(), member1.getNickname());

        //then
        assertThat(result).isFalse();
    }

    @Test
    public void ID를_멤버가_사용중O() throws Exception {
        //given
        Member member1 = createMember();
        em.persist(member1);

        Member member2 = createMember();
        em.persist(member2);

        //when
        Boolean result = memberRepository.isActiveById(member1.getId());

        //then
        assertThat(result).isTrue();
    }

    @Test
    public void ID를_멤버가_사용중OX() throws Exception {
        //given
        Member member1 = createMember();
        em.persist(member1);

        Member member2 = createMember();
        em.persist(member2);

        //when
        Boolean result = memberRepository.isActiveById(3L);

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
        ProviderAccount providerAccountNonExists = new ProviderAccount(Provider.GOOGLE, "google2");

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

        //when
        memberRepository.deleteByStatus();

        //then
        em.clear();
        Member findMember = em.find(Member.class, member.getId());
        System.out.println(findMember == null);
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
        ProviderAccount providerAccount = new ProviderAccount(Provider.GOOGLE,"google1");
        return providerAccount;
    }
}