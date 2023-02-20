package com.todoary.ms.src.web.dto;


import com.todoary.ms.src.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private String profileImgUrl;
    private String nickname;
    private String introduce;
    private String email;

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getProfileImgUrl(), member.getNickname(), member.getIntroduce(), member.getEmail());
    }
}
