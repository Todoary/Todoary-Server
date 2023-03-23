package com.todoary.ms.src.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.ProviderAccount;
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

    public Boolean isProviderAccountUsed(ProviderAccount providerAccount) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(member)
                .where(member.providerAccount.eq(providerAccount))
                .fetchFirst();
        return fetchOne != null;
    }

    public Boolean isEmailOfGeneralMemberUsed(String email) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(member)
                .where(member.email.eq(email), member.providerAccount.eq(ProviderAccount.none()))
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

    public Optional<Member> findOauthMemberByProviderAccount(ProviderAccount providerAccount) {
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

    public void removeMember(Member member) {
        em.remove(member);
    }

    public List<Member> findMemberDeactivatedTimeBefore(LocalDateTime time) {
        return em.createQuery("select m from Member m where m.status = 0 and m.modifiedAt < :time", Member.class)
                .setParameter("time", time)
                .getResultList();
    }

    public boolean existById(Long memberId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(member)
                .where(member.id.eq(memberId))
                .fetchFirst();
        return fetchOne != null;
    }
}
