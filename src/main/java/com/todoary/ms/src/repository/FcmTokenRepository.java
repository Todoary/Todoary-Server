package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.token.FcmToken;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

@Repository
public class FcmTokenRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(FcmToken fcmToken) {
        em.persist(fcmToken);
    }

    public List<FcmToken> findAllByRemindDate(LocalDate targetDate) {
        // 1. 모두 조인
        return em.createQuery("select f from FcmToken f join f.member m join m.remindAlarm r where m.remindAlarmEnable = true and r.targetDate = :targetDate", FcmToken.class)
                .setParameter("targetDate", targetDate)
                .getResultList();

        /* 2. 두 엔티티 조인 후, Member에서 fcmToken 찾음
        em.createQuery("select m from Member m join m.remindAlarm r where m.remindAlarmEnable = true and r.targetDate = :targetDate", Member.class)
                .setParameter("targetDate", targetDate)
                .getResultStream()
                .map(member -> member.getFcmToken())
                .collect(Collectors.toList());
        */

        /* 3-1. FcmToken findAll 후에, 연관관계 통해 filtering by Stream
        em.createQuery("select f from FcmToken f", FcmToken.class)
                .getResultStream()
                .filter(fcmToken -> fcmToken.getMember().getRemindAlarmEnable().equals(true) && fcmToken.getMember().getRemindAlarm().getTargetDate().equals(targetDate))
                .collect(Collectors.toList());
        */

        /* 3-2. Stream 없이
        List<FcmToken> fcmTokens = em.createQuery("select f from FcmToken f", FcmToken.class)
                .getResultList();

        List<FcmToken> findTokens = new ArrayList<>();
        for (FcmToken fcmToken : fcmTokens) {
            if (fcmToken.getMember().getRemindAlarmEnable().equals(true)
            && fcmToken.getMember().getRemindAlarm().getTargetDate().equals(targetDate)) {
                findTokens.add(fcmToken);
            }
        }

        return findTokens;
        */
    }

    public List<FcmToken> findAllByDailyEnabled() {
        return em.createQuery("select f from FcmToken f join f.member m where m.dailyAlarmEnable = true and f.id is not null", FcmToken.class)
                .getResultList();
    }
}
