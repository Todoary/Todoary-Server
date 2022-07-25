package com.todoary.ms.src.category.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.todoary.ms.src.category.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "title", "color"})
public class GetCategoryRes {
    private Long id;
    private String title;
    private String color;

}