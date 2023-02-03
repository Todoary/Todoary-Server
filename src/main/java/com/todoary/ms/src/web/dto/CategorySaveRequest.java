package com.todoary.ms.src.web.dto;

import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Color;
import com.todoary.ms.src.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

import static com.todoary.ms.util.ColumnLengthInfo.CATEGORY_TITLE_MAX_LENGTH;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class CategorySaveRequest {
    @Length(max = CATEGORY_TITLE_MAX_LENGTH, message="TITLE_TOO_LONG")
    private String title;
    @NotNull(message = "EMPTY_COLOR_CATEGORY")
    private Integer color;
    public CategorySaveRequest(String title, Integer color) {
        this.title = title;
        this.color = color;
    }

    public Category toEntity(Member member) {
        return new Category(title, new Color(color), member);
    }
}

