package com.todoary.ms.src.web.dto.diary;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class StickersRequest {

    @Valid
    private List<StickerRequest> created;
    @Valid
    private List<StickerUpdateRequest> modified;
    @Valid
    private List<StickerDeleteRequest> deleted;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @ToString
    public static class StickerUpdateRequest {
        @NotNull(message = "EMPTY_STICKER_ID")
        private Long id;
        @NotNull(message = "EMPTY_STICKER_TYPE")
        private Integer stickerId;
        private Double locationX = 0.0;
        private Double locationY = 0.0;
        private Double width = 0.0;
        private Double height = 0.0;
        private Double rotation = 0.0;
        private Boolean flipped = false;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @ToString
    public static class StickerDeleteRequest {
        @NotNull(message = "EMPTY_STICKER_ID")
        private Long id;
    }
}
