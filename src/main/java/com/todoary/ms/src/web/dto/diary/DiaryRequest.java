package com.todoary.ms.src.web.dto.diary;


import lombok.*;
import org.hibernate.validator.constraints.Length;

import static com.todoary.ms.src.common.util.ColumnLengthInfo.DIARY_TITLE_MAX_LENGTH;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder
public class DiaryRequest {

    @Length(max = DIARY_TITLE_MAX_LENGTH, message="DIARY_TITLE_TOO_LONG")
    private String title;

    private String content;


    public DiaryRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
