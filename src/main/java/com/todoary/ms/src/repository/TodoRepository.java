package com.todoary.ms.src.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.todoary.ms.src.domain.QTodo.todo;

@Repository
public class TodoRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Autowired
    public TodoRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

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
        return em.createQuery("select t from Todo t where t.member = :member and t.targetDate = :targetDate order by t.targetDate, t.targetTime, t.createdAt", Todo.class)
                .setParameter("member", member)
                .setParameter("targetDate", targetDate)
                .getResultList();
    }

    public List<Todo> findByCategoryAndSatisfy(Category category, Predicate condition) {
        return queryFactory.selectFrom(todo)
                .where(todo.category.eq(category))
                .where(condition)
                .orderBy(todo.targetDate.asc(), todo.targetTime.asc().nullsLast(), todo.createdAt.asc())
                .fetch();
    }

    public Slice<Todo> findSliceByCategoryAndSatisfy(Pageable pageable, Category category, Predicate condition) {
        List<Todo> result = queryFactory.selectFrom(todo)
                .where(todo.category.eq(category))
                .where(condition)
                .offset(pageable.getOffset())
                // slice는 카운트 쿼리가 없는 대신 게시물이 더 존재하는 지 알아야 하므로 1개 더 가져온다
                .limit(pageable.getPageSize() + 1)
                .orderBy(todo.targetDate.asc(), todo.targetTime.asc().nullsLast(), todo.createdAt.asc())
                .fetch();
        return checkLastSlice(result, pageable);
    }

    private Slice<Todo> checkLastSlice(List<Todo> result, Pageable pageable) {
        boolean hasNext = false;
        if (result.size() > pageable.getPageSize()) {
            hasNext = true;
            // 요청한 만큼만 응답하기 위해 제거
            result.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(result, pageable, hasNext);
    }

    public List<Todo> findBetweenDaysAndMember(LocalDate firstDay, LocalDate lastDay, Member member) {
        return em.createQuery("select t from Todo t where t.member = :member and " +
                                      "t.targetDate between :firstDay and :lastDay order by t.targetDate", Todo.class)
                .setParameter("member", member)
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .getResultList();
    }

    public List<Todo> findAllByDate(LocalDate targetDate) {
        return em.createQuery("select t from Todo t where t.targetDate = :targetDate", Todo.class)
                .setParameter("targetDate", targetDate)
                .getResultList();
    }
}
