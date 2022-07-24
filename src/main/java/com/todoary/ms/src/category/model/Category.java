package com.todoary.ms.src.category.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Long id;
    //private Long categoryImgId;
    private String title;
    private String color;

    public Category(long id, long category_img_id, String title, String color){

        this.title = title;
        this.color = color;
    }

}
