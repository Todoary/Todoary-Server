package com.todoary.ms.src.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Provider;
import com.todoary.ms.src.domain.ProviderAccount;
import com.todoary.ms.src.domain.token.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.todoary.ms.src.domain.QMember.member;

@Repository
public class MemberRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Autowired
    public MemberRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Optional<Member> findById(Long memberId) {
        return Optional.ofNullable(em.find(Member.class, memberId));
    }

    public Boolean isProviderAccountAndEmailUsed(ProviderAccount providerAccount, String email) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(member)
                .where(member.email.eq(email), member.providerAccount.eq(providerAccount))
                .fetchFirst();
        return fetchOne != null;
    }

    public Boolean isNicknameUsed(String nickname) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(member)
                .where(member.nickname.eq(nickname))
                .fetchFirst();
        return fetchOne != null;
    }

    public Boolean isActiveByProviderAccount(ProviderAccount providerAccount) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(member)
                .where(member.providerAccount.provider.eq(providerAccount.getProvider()))
                .where(member.providerAccount.providerId.eq(providerAccount.getProviderId()))
                .where(member.status.eq(1))
                .fetchFirst();
        return fetchOne != null;
    }

    public void deleteByStatus() {
        LocalDateTime startDateTime = LocalDateTime.of(
                LocalDate.now().minusDays(30)
                , LocalTime.of(0, 0, 0));

        LocalDateTime endDateTime = LocalDateTime.of(
                LocalDate.now().minusDays(29)
                , LocalTime.of(0, 0, 0));

        em.createQuery("delete from Member m " +
                               "where m.status = 0 " +
                               "and m.modifiedAt >= :startDateTime " +
                               "and m.modifiedAt < :endDateTime")
                .setParameter("startDateTime", startDateTime)
                .setParameter("endDateTime", endDateTime)
                .executeUpdate();
    }

    public Boolean existByRefreshToken(RefreshToken refreshToken) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(member)
                .where(member.refreshToken.code.eq(refreshToken.getCode()))
                .fetchFirst();
        return fetchOne != null;
    }

    public Optional<Member> findByEmail(String email) {
        return em.createQuery("select m from Member m where m.email = :email and m.status = 1", Member.class)
                .setParameter("email", email)
                .getResultStream().findAny();
    }

    public Optional<Member> findByProviderEmail(Provider provider, String email) {
        return em.createQuery("select m from Member m where m.providerAccount.provider = :provider and m.email = :email", Member.class)
                .setParameter("provider", provider)
                .setParameter("email", email)
                .getResultStream().findAny();
    }

    public Optional<Member> findByProviderAccount(ProviderAccount providerAccount) {
        return em.createQuery("select m from Member m where m.providerAccount = :providerAccount", Member.class)
                .setParameter("providerAccount", providerAccount)
                .getResultStream()
                .findAny();
    }

    public List<Member> findAllDailyAlarmEnabled() {
        return em.createQuery("select m from Member m where m.dailyAlarmEnable = true", Member.class)
                .getResultList();
    }

    public Optional<Member> findGeneralMemberByEmail(String email) {
        return em.createQuery("select m from Member m where m.providerAccount = :providerAccount and m.email = :email", Member.class)
                .setParameter("providerAccount", ProviderAccount.none())
                .setParameter("email", email)
                .getResultStream()
                .findAny();
    }

    public List<Member> findAllForRemindAlarm(LocalDate targetDate) {
        return em.createQuery("select m from Member m where m.remindAlarm.targetDate = :targetDate and m.remindAlarmEnable = true", Member.class)
                .setParameter("targetDate", targetDate)
                .getResultList();
    }

    public Optional<Member> findByEmailAndProviderAccount(String email, ProviderAccount providerAccount) {
        return em.createQuery("select m from Member m " +
                "where m.email = :email " +
                "and m.providerAccount.provider = :provider " +
                "and m.providerAccount.providerId = :providerId")
                .setParameter("email", email)
                .setParameter("provider", providerAccount.getProvider())
                .setParameter("providerId", providerAccount.getProviderId())
                .getResultStream()
                .findAny();
    }

}
