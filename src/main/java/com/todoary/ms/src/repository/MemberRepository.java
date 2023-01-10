package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Provider;
import com.todoary.ms.src.domain.ProviderAccount;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class MemberRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Optional<Member> findById(Long memberId) {
        return Optional.ofNullable(em.find(Member.class, memberId));
    }

    public Boolean isProviderEmailUsed(Provider provider, String email) {
        try {
            em.createQuery("select m from Member m where m.email = :email and m.providerAccount.provider = :provider", Member.class)
                    .setParameter("email", email)
                    .setParameter("provider", provider)
                    .getSingleResult();

            return true;
        } catch (NoResultException exception) {
            return false;
        }
    }

    public Boolean isNicknameUsed(String nickname) {
        try {
            em.createQuery("select m from Member m where m.nickname = :nickname", Member.class)
                    .setParameter("nickname", nickname)
                    .getSingleResult();

            return true;
        } catch (NoResultException exception) {
            return false;
        }
    }

    public Boolean isNicknameUsedByOthers(Long memberId ,String nickname) {
        try {
            em.createQuery("select m from Member m where m.nickname = :nickname and m.id <> :memberId", Member.class)
                    .setParameter("nickname", nickname)
                    .setParameter("memberId", memberId)
                    .getSingleResult();
            return true;
        } catch (NoResultException exception) {
            return false;
        }
    }

    public Boolean isActiveById(Long memberId) {
        try {
            em.createQuery("select m from Member m where m.id = :memberId and m.status = 1", Member.class)
                    .setParameter("memberId", memberId)
                    .getSingleResult();

            return true;
        } catch (NoResultException exception) {
            return false;
        }
    }

    public Boolean isActiveByProviderAccount(ProviderAccount providerAccount) {
        try {
            em.createQuery("select m from Member m " +
                            "where m.providerAccount.provider = :provider " +
                            "and m.providerAccount.providerId = :providerId " +
                            "and m.status = 1", Member.class)
                    .setParameter("provider", providerAccount.getProvider())
                    .setParameter("providerId", providerAccount.getProviderId())
                    .getSingleResult();

            return true;
        } catch (NoResultException exception) {
            return false;
        }
    }

    public void deleteByProviderEmail(Provider provider, String email) {
        em.createQuery("delete from Member m where m.providerAccount.provider = :provider and m.email = :email")
                .setParameter("provider", provider)
                .setParameter("email", email)
                .executeUpdate();
    }

    public void deleteByStatus() {
//        /* MySQL
//        em.createNativeQuery("delete from member " +
//                "where member_id in " +
//                "   (select member_id " +
//                "   from member " +
//                "   where status = 0) " +
//                "and DATE_FORMAT(DATE_ADD(modified_at, INTERVAL 30 DAY), '%Y-%m-%d')  = current_date")
//                .executeUpdate();
//        */

        // H2
        em.createNativeQuery("delete from member " +
                        "where member_id in " +
                        "   (select member_id " +
                        "   from member " +
                        "   where status = 0) " +
                        "and FORMATDATETIME(TIMESTAMPADD(DAY, 0, modified_at), 'yyyy-MM-dd')  = current_date")
                .executeUpdate();
    }
}
