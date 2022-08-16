package com.todoary.ms.src.sticker.dto;

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
    private String locationX;
    private String locationY;
    private String created_at;

}
