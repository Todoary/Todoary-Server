package com.todoary.ms.src.category.model;

import com.todoary.ms.src.user.dto.PostUserReq;
import com.todoary.ms.src.user.model.User;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Long id;
    private Long categoryImgId;
    private String title;
    private String color;

    public Category(String title, String color){
        this.title = title;
        this.color = color;
    }

}
