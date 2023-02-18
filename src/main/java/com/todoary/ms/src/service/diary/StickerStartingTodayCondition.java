package com.todoary.ms.src.service.diary;

import com.querydsl.core.types.Predicate;

import java.time.LocalDate;

import static com.todoary.ms.src.domain.QDiary.diary;


public class StickerStartingTodayCondition implements StickerByDiaryCondition {

    @Override
    public Predicate getPredicate() {
        return diary.createdDate.goe(LocalDate.now());
    }
}
