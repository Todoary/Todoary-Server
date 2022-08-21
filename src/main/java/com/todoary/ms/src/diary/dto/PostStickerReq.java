package com.todoary.ms.src.diary.dto;


import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostStickerReq {

    private Integer stickerId;
    private Double locationX;
    private Double locationY;
    private Double width;
    private Double height;
    private Double rotation;
    private boolean flipped;
}
