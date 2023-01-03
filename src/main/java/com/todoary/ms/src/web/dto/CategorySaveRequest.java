package com.todoary.ms.src.web.dto;

import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Color;
import com.todoary.ms.src.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategorySaveRequest {
    private String title;
    private Integer color;

    public CategorySaveRequest(String title, Integer color) {
        this.title = title;
        this.color = color;
    }

    public Category toEntity(Member member) {
        return new Category(title, new Color(color), member);
    }
}

