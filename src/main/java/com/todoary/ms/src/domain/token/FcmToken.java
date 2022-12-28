package com.todoary.ms.src.domain.token;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("fcm")
public class FcmToken extends Token {
    private String fcmToken;
}
