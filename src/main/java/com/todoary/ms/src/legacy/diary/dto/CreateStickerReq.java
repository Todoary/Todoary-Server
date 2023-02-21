package com.todoary.ms.src.legacy.diary.dto;


import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateStickerReq {
    private Long stickerId;
    private Double locationX;
    private Double locationY;
    private Double width;
    private Double height;
    private Double rotation;
    private boolean flipped;
}
