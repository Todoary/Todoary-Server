package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class CategoryRepository {
    @PersistenceContext
    private EntityManager em;

    public Category save(Category category) {
        em.persist(category);
        return category;
    }

    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(em.find(Category.class, id));
    }

    public void delete(Category category) {
        em.remove(category);
    }

    public Optional<Category> findByMembersTitle(Member member, String title) {
        return em.createQuery("select c from Category c where c.member = :member and c.title = :title", Category.class)
                .setParameter("member", member)
                .setParameter("title", title)
                .getResultStream()
                .findFirst();
    }

}
