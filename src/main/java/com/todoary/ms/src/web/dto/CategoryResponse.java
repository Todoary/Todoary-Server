package com.todoary.ms.src.web.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String title;
    private Integer color;
}
