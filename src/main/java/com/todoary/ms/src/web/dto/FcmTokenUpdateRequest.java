package com.todoary.ms.src.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FcmTokenUpdateRequest {
    @NotNull(message="MEMBERS_EMPTY_FCM_TOKEN")
    private String fcmToken;
}
