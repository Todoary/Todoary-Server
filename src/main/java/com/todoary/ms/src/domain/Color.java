package com.todoary.ms.src.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
@Embeddable
public class Color {
    @Column(name = "color")
    private Integer code;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Color)
            return this.getCode().equals(((Color) obj).getCode());
        return false;
    }

    @Override
    public int hashCode() {
        return this.getCode().hashCode();
    }
}
