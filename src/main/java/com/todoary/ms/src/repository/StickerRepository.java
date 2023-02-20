package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.Sticker;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class StickerRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Sticker> saveAll(List<Sticker> stickers) {
        stickers.forEach(sticker -> em.persist(sticker));
        return stickers;
    }

    public Optional<Sticker> findById(Long id) {
        return Optional.ofNullable(em.find(Sticker.class, id));
    }

    public void delete(Sticker sticker) {
        em.remove(sticker);
    }
}
