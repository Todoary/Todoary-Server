package com.todoary.ms.src.legacy.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetDiaryByDateRes {

    private Long diaryId;
    private String title;
    private String content;
    private String created_at;

}
