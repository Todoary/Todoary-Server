package com.todoary.ms.src.web.dto.diary;

import com.todoary.ms.src.domain.Diary;
import com.todoary.ms.src.domain.Sticker;
import com.todoary.ms.src.domain.StickerShape;
import com.todoary.ms.src.domain.StickerType;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class StickerRequest {
    @NotNull(message = "EMPTY_STICKER_TYPE")
    private Integer stickerId;
    private Double locationX = 0.0;
    private Double locationY = 0.0;
    private Double width = 0.0;
    private Double height = 0.0;
    private Double rotation = 0.0;
    private Boolean flipped = false;

    public Sticker toEntity(Diary diary) {
        return Sticker.builder()
                .diary(diary)
                .type(StickerType.from(stickerId))
                .shape(StickerShape.builder()
                               .locationX(locationX)
                               .locationY(locationY)
                               .width(width)
                               .height(height)
                               .rotation(rotation)
                               .flipped(flipped)
                               .build())
                .build();
    }
}
