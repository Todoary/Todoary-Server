package com.todoary.ms.src.diary.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostStickerReq {

    private Double locationX;
    private Double locationY;
    private Double width;
    private Double height;
    private Double rotation;
    private boolean flipped;
}
