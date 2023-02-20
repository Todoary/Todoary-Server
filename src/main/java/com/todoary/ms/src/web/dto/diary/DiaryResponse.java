package com.todoary.ms.src.web.dto.diary;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.todoary.ms.src.domain.Diary;
import lombok.*;

import java.time.LocalDate;


@ToString
@Getter
@AllArgsConstructor @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class DiaryResponse {

    private Long diaryId;

    private String title;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate createdDate;

    public static DiaryResponse from(Diary diary) {
        return DiaryResponse.builder()
                .diaryId(diary.getId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .createdDate(diary.getCreatedDate())
                .build();
    }
}
