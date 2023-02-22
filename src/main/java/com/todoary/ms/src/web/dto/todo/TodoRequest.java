package com.todoary.ms.src.web.dto.todo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Todo;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.todoary.ms.src.common.util.ColumnLengthInfo.TODO_TITLE_MAX_LENGTH;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Builder
public class TodoRequest {

    @Length(max = TODO_TITLE_MAX_LENGTH, message="TODO_TITLE_TOO_LONG")
    private String title;

    @Builder.Default
    private Boolean isAlarmEnabled = false;

    @NotNull(message="EMPTY_TODO_DATE")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate targetDate;

    @DateTimeFormat(pattern="HH:mm")
    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime targetTime;

    @NotNull(message="USERS_CATEGORY_NOT_EXISTS")
    private Long categoryId;

    public Todo toEntity(Member member, Category category) {
        System.out.println("this = " + this);
        return Todo.builder()
                .title(title)
                .isAlarmEnabled(isAlarmEnabled)
                .category(category)
                .member(member)
                .targetDate(targetDate)
                .targetTime(targetTime)
                .build();
    }
}
