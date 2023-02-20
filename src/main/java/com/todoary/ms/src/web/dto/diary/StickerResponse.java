package com.todoary.ms.src.web.dto.diary;


import com.todoary.ms.src.domain.Sticker;
import lombok.*;

import javax.persistence.Column;

@ToString
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE) @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class StickerResponse {

    private Long id;
    private Long diaryId;
    private Integer stickerId;

    private Double locationX;

    private Double locationY;

    private Double width;

    private Double height;

    private Double rotation;

    @Column(nullable = false)
    @Builder.Default
    private Boolean flipped = false;

    public static StickerResponse from(Sticker sticker) {
        return StickerResponse.builder()
                .id(sticker.getId())
                .diaryId(sticker.getDiary().getId())
                .stickerId(sticker.getType().getCode())
                .locationX(sticker.getShape().getLocationX())
                .locationY(sticker.getShape().getLocationY())
                .width(sticker.getShape().getWidth())
                .height(sticker.getShape().getHeight())
                .rotation(sticker.getShape().getRotation())
                .flipped(sticker.getShape().getFlipped())
                .build();
    }
}
