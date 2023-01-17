package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.token.RefreshToken;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class RefreshTokenRepository {
    @PersistenceContext
    private EntityManager em;

    public Long save(RefreshToken refreshToken) {
        em.persist(refreshToken);
        return refreshToken.getId();
    }

    public Boolean existsByCode(String code) {
        return em.createQuery("select r from RefreshToken r where r.code = :code", RefreshToken.class)
                .setParameter("code", code)
                .getResultList()
                .size() == 1;
    }

    public Optional<RefreshToken> findByMemberId(Long memberId) {
        List<RefreshToken> findRefreshTokens = em.createQuery("select r from RefreshToken r join r.member m where m.id = :memberId", RefreshToken.class)
                .setParameter("memberId", memberId)
                .getResultList();

        if (findRefreshTokens.size() == 1) {
            return Optional.ofNullable(findRefreshTokens.get(0));
        }

        return Optional.empty();
    }
}
