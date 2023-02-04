package com.todoary.ms.src.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Todo;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.todoary.ms.util.ColumnLengthInfo.TODO_TITLE_MAX_LENGTH;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Builder
public class TodoRequest {

    @Length(max = TODO_TITLE_MAX_LENGTH, message="TODO_TITLE_TOO_LONG")
    private String title;

    @JsonProperty("isAlarmEnabled")
    private boolean isAlarmEnabled;

    @NotNull(message="EMPTY_TODO_DATE")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate targetDate;

    // 직렬화 형식 지정
    @DateTimeFormat(pattern="HH:mm")
    private LocalTime targetTime;

    @NotNull(message="USERS_CATEGORY_NOT_EXISTS")
    private Long categoryId;

    public Todo toEntity(Member member, Category category) {
        return Todo.builder()
                .title(getTitle())
                .isAlarmEnabled(isAlarmEnabled())
                .category(category)
                .member(member)
                .targetDate(targetDate)
                .targetTime(targetTime)
                .build();
    }
}
