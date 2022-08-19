package com.todoary.ms.src.diary.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "userId", "diaryId","sticker","created_at"})
public class GetStickerRes {

    private Long id;
    private Long userId;
    private Long diaryId;
    private Integer stickerId;
    private Double locationX;
    private Double locationY;
    private Double width;
    private Double height;
    private Double rotation;
    private boolean flipped;
    private String created_date;

}
