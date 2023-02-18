package com.todoary.ms.src.web.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.todoary.ms.src.domain.Sticker;
import lombok.*;

import javax.persistence.Column;
import java.time.LocalDate;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class StickerResponse {

    private Long diaryId;

    private Long stickerId;

    private Double locationX;

    private Double locationY;

    private Double width;

    private Double height;

    private Double rotation;

    @Column(nullable = false)
    private Boolean flipped = false;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate createdDate;

    @Builder
    public StickerResponse(Long diaryId, Long stickerId, Double locationX, Double locationY, Double width, Double height, Double rotation, Boolean flipped, LocalDate createdDate){
        this.diaryId=diaryId;
        this.stickerId=stickerId;
        this.locationX=locationX;
        this.locationY=locationY;
        this.width=width;
        this.height=height;
        this.rotation=rotation;
        this.flipped=flipped;
        this.createdDate=createdDate;
    }

    public static StickerResponse from(Sticker sticker) {
        return StickerResponse.builder()
                .diaryId(sticker.getId())
                .stickerId(sticker.getDiary().getId())
                .locationX(sticker.getLocationX())
                .locationY(sticker.getLocationY())
                .width(sticker.getWidth())
                .height(sticker.getHeight())
                .rotation(sticker.getRotation())
                .flipped(sticker.getFlipped())
                .createdDate(sticker.getDiary().getCreatedDate())
                .build();
    }

}
