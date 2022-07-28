package com.todoary.ms.src.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetDiaryByDateRes {

    private long diaryId;
    private String title;
    private String content;
    private String created_at;

}
