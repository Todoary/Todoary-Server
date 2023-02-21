package com.todoary.ms.src.domain;

import com.todoary.ms.src.web.dto.diary.StickerRequest;
import com.todoary.ms.src.web.dto.diary.StickersRequest.StickerUpdateRequest;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Embeddable
public class StickerShape {

    @Column(name = "locationX")
    @Builder.Default
    private Double locationX = 0.0;

    @Column(name = "locationY")
    @Builder.Default
    private Double locationY = 0.0;

    @Column(name = "width")
    @Builder.Default
    private Double width = 0.0;

    @Column(name = "height")
    @Builder.Default
    private Double height = 0.0;

    @Column(name = "rotation")
    @Builder.Default
    private Double rotation = 0.0;

    @Column(name = "flipped")
    @Builder.Default
    private Boolean flipped = false;

    public static StickerShape from(StickerRequest request) {
        return StickerShape.builder()
                .locationX(request.getLocationX())
                .locationY(request.getLocationY())
                .width(request.getWidth())
                .height(request.getHeight())
                .rotation(request.getRotation())
                .flipped(request.getFlipped())
                .build();
    }

    public static StickerShape from(StickerUpdateRequest request) {
        return StickerShape.builder()
                .locationX(request.getLocationX())
                .locationY(request.getLocationY())
                .width(request.getWidth())
                .height(request.getHeight())
                .rotation(request.getRotation())
                .flipped(request.getFlipped())
                .build();
    }
}
