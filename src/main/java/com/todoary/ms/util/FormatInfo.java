package com.todoary.ms.util;

import lombok.Getter;

@Getter
public enum FormatInfo {
    CATEGORY_TITLE_LENGTH(10),
    DIARY_TITLE_LENGTH(20),
    TODO_TITLE_LENGTH(20);

    private final int length;

    private FormatInfo(int length) {
        this.length = length;
    }
}
