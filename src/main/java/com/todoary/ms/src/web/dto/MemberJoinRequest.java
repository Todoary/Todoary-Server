package com.todoary.ms.src.web.dto;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberJoinRequest {
    private String name;
    private String nickname;
    private String email;
    private String password;
    @Builder.Default
    private Boolean isTermsEnable = true;
}
