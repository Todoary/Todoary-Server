package com.todoary.ms.src.category.dto;

import com.todoary.ms.src.category.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCategoryRes {
//    private Long id;
//    private String title;
//    private String color;
    private List<Category> categories;

}