package com.todoary.ms.src.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
@EqualsAndHashCode(of = {"code"})
@Embeddable
public class Color {
    @Column(name = "color")
    private Integer code;
}
