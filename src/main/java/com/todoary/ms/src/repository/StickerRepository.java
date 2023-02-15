package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.Diary;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Sticker;
import com.todoary.ms.src.domain.Todo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class StickerRepository {


    @PersistenceContext
    private EntityManager em;

    public Sticker save(Sticker sticker) {
        em.persist(sticker);
        return sticker;
    }

    public Optional<Sticker> findById(Long id) {
        return Optional.ofNullable(em.find(Sticker.class, id));
    }

    public void delete(Sticker sticker) {
        em.remove(sticker);
    }

    public List<Sticker> findByDiary(Diary diary) {
        return em.createQuery("select s from sticker s where s.diary = :diary order by s.created_date", Sticker.class)
                .setParameter("diary", diary)
                .getResultList();
    }
}
