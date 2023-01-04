package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Todo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class TodoRepository {
    @PersistenceContext
    private EntityManager em;

    public Todo save(Todo todo) {
        em.persist(todo);
        return todo;
    }

    public Optional<Todo> findById(Long id) {
        return Optional.ofNullable(em.find(Todo.class, id));
    }

    public void delete(Todo todo) {
        em.remove(todo);
    }

    public List<Todo> findByDateAndMember(LocalDate targetDate, Member member) {
        return em.createQuery("select t from Todo t where t.member = :member and t.targetDate = :targetDate", Todo.class)
                .setParameter("member", member)
                .setParameter("targetDate", targetDate)
                .getResultList();
    }

    public List<Todo> findByCategory(Category category) {
        return em.createQuery("select t from Todo t where t.category = :category", Todo.class)
                .setParameter("category", category)
                .getResultList();
    }

    public List<Todo> findBetweenDaysAndMember(LocalDate firstDay, LocalDate lastDay, Member member) {
        return em.createQuery("select t from Todo t where t.member = :member and " +
                        "t.targetDate between :firstDay and :lastDay", Todo.class)
                .setParameter("member", member)
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .getResultList();
    }
}
