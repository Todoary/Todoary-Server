package com.todoary.ms.src.web.dto.category;

import lombok.*;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class CategoryResponse {
    private Long id;
    private String title;
    private Integer color;
}
