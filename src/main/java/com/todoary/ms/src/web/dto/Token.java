package com.todoary.ms.src.web.dto;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class Token {
    private String accessToken;
    private String refreshToken;
}
