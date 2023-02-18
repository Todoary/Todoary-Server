package com.todoary.ms.src.repository;


import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.todoary.ms.src.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.todoary.ms.src.domain.QSticker.sticker;

@Repository
public class DiaryRepository {

    @PersistenceContext
    private EntityManager em;

    private final JPAQueryFactory queryFactory;

    public DiaryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }


    public Diary save(Diary diary) {
        em.persist(diary);
        return diary;
    }

    public Optional<Diary> findById(Long id) {
        return Optional.ofNullable(em.find(Diary.class, id));
    }


    public Optional<Diary> findByDate(LocalDate createdDate) {
        return Optional.ofNullable(em.find(Diary.class, createdDate));
    }


    public void delete(Diary diary) {
        em.remove(diary);
    }

    public List<Diary> findBetweenDaysAndMember(LocalDate firstDay, LocalDate lastDay, Member member) {
        return em.createQuery("select d from Diary d where d.member = :member and " +
                        "d.createdDate between :firstDay and :lastDay order by d.createdDate", Diary.class)
                .setParameter("member", member)
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .getResultList();
    }

    public List<Sticker> findStickersByDiary(Diary diary, Predicate condition) {
        return queryFactory.selectFrom(sticker)
                .where(sticker.diary.eq(diary))
                .where(condition)
                .orderBy(sticker.createdAt.asc())
                .fetch();
    }

}
