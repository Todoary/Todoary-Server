package com.todoary.ms.src.domain.token;

import javax.persistence.*;

@Entity
@DiscriminatorValue("refresh")
public class RefreshToken extends Token {
    private String refreshToken;
}
