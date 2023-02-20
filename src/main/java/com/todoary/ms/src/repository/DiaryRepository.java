package com.todoary.ms.src.repository;


import com.todoary.ms.src.domain.Diary;
import com.todoary.ms.src.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class DiaryRepository {

    @PersistenceContext
    private EntityManager em;

    public Diary save(Diary diary) {
        em.persist(diary);
        return diary;
    }

    public Optional<Diary> findById(Long id) {
        return Optional.ofNullable(em.find(Diary.class, id));
    }

    public Optional<Diary> findByMemberAndDate(Member member, LocalDate createdDate) {
        return em.createQuery("select d from Diary d where d.member = :member and d.createdDate = :createdDate", Diary.class)
                .setParameter("member", member)
                .setParameter("createdDate", createdDate)
                .getResultStream().findAny();
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
}
