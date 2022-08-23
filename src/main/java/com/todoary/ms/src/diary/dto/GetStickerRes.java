package com.todoary.ms.src.diary.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "diaryId","stickerId","locationX","locationY","width","height","rotation","flipped","created_date"})
public class GetStickerRes {

    private Long id;
    private Long diaryId;
    private Long stickerId;
    private Double locationX;
    private Double locationY;
    private Double width;
    private Double height;
    private Double rotation;
    private boolean flipped;
    private String created_date;

}
