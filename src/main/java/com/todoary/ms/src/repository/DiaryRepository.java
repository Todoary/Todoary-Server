package com.todoary.ms.src.repository;


import com.todoary.ms.src.domain.Diary;
import com.todoary.ms.src.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
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

    public Optional<Diary> findByDateAndMember(LocalDate created_date, Member member) {
        return Optional.ofNullable(em.find(Diary.class, created_date));
    }

    public void delete(Diary diary) {
        em.remove(diary);
    }


}
