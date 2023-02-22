package com.todoary.ms.src.legacy.category.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "title", "color"})
public class GetCategoryRes {
    private Long id;
    private String title;
    private Integer color;

}