package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.token.RefreshToken;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class RefreshTokenRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(RefreshToken refreshToken) {
        em.persist(refreshToken);
    }

    public Boolean isExistsByValue(String token) {
        try {
            em.createQuery("select r from RefreshToken r where r.token = :token", RefreshToken.class)
                    .setParameter("token", token)
                    .getSingleResult();

            return true;
        } catch (NoResultException e) {
            return false;
        }
    }
}
