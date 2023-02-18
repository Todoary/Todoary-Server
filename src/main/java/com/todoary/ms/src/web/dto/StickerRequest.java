package com.todoary.ms.src.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todoary.ms.src.domain.Diary;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Sticker;
import com.todoary.ms.src.domain.StickerType;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Builder
public class StickerRequest
{

    @NotNull(message = "EMPTY_STICKER_DIARY")
    private Integer stickerId;

    private Double locationX;

    private Double locationY;

    private Double width;

    private Double height;

    private Double rotation;

    @JsonProperty("isAlarmEnabled")
    private Boolean flipped;

    public Sticker toEntity(Member member, Diary diary, StickerType stickerType) {
        return Sticker.builder()
                .locationX(getLocationX())
                .locationY(getLocationY())
                .width(getWidth())
                .height(getHeight())
                .rotation(getRotation())
                .flipped(getFlipped())
                .build();
    }

}
