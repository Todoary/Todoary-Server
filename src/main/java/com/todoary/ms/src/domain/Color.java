package com.todoary.ms.src.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Embeddable
public class Color {
    @Column(name = "color", nullable = false)
    private Integer code = 1;

    public static Color from(int code) {
        return new Color(code);
    }
}
