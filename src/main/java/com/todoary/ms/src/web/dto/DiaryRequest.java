package com.todoary.ms.src.web.dto;



import com.todoary.ms.src.domain.Diary;
import com.todoary.ms.src.domain.Member;
import io.swagger.annotations.Info;
import lombok.*;
import org.hibernate.validator.constraints.Length;


import static com.todoary.ms.util.ColumnLengthInfo.DIARY_TITLE_MAX_LENGTH;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder
public class DiaryRequest {

    @Length(max = DIARY_TITLE_MAX_LENGTH, message="DIARY_TITLE_TOO_LONG")
    private String title;

    private String content;


    public DiaryRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }


    public Diary toEntity(Member member) {
        return new Diary(title, content,member);
    }
}
