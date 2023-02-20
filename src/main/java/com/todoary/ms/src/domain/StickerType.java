package com.todoary.ms.src.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Embeddable
public class StickerType {
    @Column(name = "sticker_type", nullable = false)
    private Integer code = 1;
    public static StickerType from(int code){
        return new StickerType(code);
    }
}
