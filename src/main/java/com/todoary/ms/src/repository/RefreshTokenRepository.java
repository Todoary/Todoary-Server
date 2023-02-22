package com.todoary.ms.src.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todoary.ms.src.domain.token.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.todoary.ms.src.domain.token.QRefreshToken.refreshToken;

@Repository
public class RefreshTokenRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Autowired
    public RefreshTokenRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Long save(RefreshToken refreshToken) {
        em.persist(refreshToken);
        return refreshToken.getId();
    }

    public Boolean existsByCode(String code) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(refreshToken)
                .where(refreshToken.code.eq(code))
                .fetchFirst();
        return fetchOne != null;
    }

    public Optional<RefreshToken> findByMemberId(Long memberId) {
        return em.createQuery("select r from RefreshToken r join r.member m where m.id = :memberId", RefreshToken.class)
                .setParameter("memberId", memberId)
                .getResultStream().findAny();
    }
}
