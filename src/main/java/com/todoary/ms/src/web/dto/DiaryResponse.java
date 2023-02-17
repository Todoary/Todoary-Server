package com.todoary.ms.src.web.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.todoary.ms.src.domain.Diary;
import lombok.*;

import java.time.LocalDate;


@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class DiaryResponse {

    private Long diaryId;

    private String title;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate createdDate;

    @Builder
    public DiaryResponse(Long diaryId,String title, String content, LocalDate createdDate) {
        this.diaryId = diaryId;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
    }

    public static DiaryResponse from(Diary diary) {
        return DiaryResponse.builder()
                .diaryId(diary.getId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .createdDate(diary.getCreatedDate())
                .build();
    }
}
