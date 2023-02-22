package com.todoary.ms.src.legacy.category.model;

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
    private Integer color;

    public Category(String title, Integer color){
        this.title = title;
        this.color = color; //ffffff -> 0 -17
    }

}
