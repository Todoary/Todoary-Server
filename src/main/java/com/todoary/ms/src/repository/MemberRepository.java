package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Provider;
import com.todoary.ms.src.domain.ProviderAccount;
import com.todoary.ms.src.domain.token.RefreshToken;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public class MemberRepository {
    @PersistenceContext
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
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

    public Boolean existById(Long memberId) {
        try {
            em.createQuery("select m from Member m where m.id = :memberId and m.status = 1", Member.class)
                    .setParameter("memberId", memberId)
                    .getSingleResult();

            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    public Boolean existByRefreshToken(RefreshToken refreshToken) {
        try {
            em.createQuery("select m from Member m join m.refreshToken r where r.code = :code", Member.class)
                    .setParameter("code", refreshToken.getCode())
                    .getSingleResult();

            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    public Optional<Member> findByEmail(String email) {
        try {
            Member member = em.createQuery("select m from Member m where m.email = :email and m.status = 1", Member.class)
                    .setParameter("email", email)
                    .getSingleResult();

            return Optional.ofNullable(member);
        } catch (NoResultException e) {
            return Optional.ofNullable(null);
        }
    }

    public Optional<Member> findByProviderEmail(String email, String providerName) {
        try {
            Member member = em.createQuery("select m from Member m where m.providerAccount.provider = :provider and m.email = :email", Member.class)
                    .setParameter("provider", Provider.findByProviderName(providerName))
                    .setParameter("email", email)
                    .getSingleResult();

            return Optional.ofNullable(member);
        } catch (NoResultException e) {
            return Optional.ofNullable(null);
        }
    }
}
