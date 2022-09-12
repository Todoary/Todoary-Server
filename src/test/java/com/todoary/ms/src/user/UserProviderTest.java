package com.todoary.ms.src.user;

import com.todoary.ms.util.BaseException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserProviderTest {

    @Autowired
    UserProvider userProvider;

    @Test
    @DisplayName("FCM 토큰 존재 O 테스트")
    public void checkFcmTokenIdExist() {
        Long res = Long.valueOf(0);
        try {
            res = userProvider.checkFcmTokenExist("dVne0fv1qELSikuQsLy75l:APA91bHJ3bDdhSFJiQL05SJKhSC8v_BkdNaz2DDjFy1jYQpVu4F1UB3vjPFLeLYOPAn-UbU_uBEEY6hXO5C2zOSZFYN1jHyDYv41JV_1n9iGl5I1cTsPoeGCPYLASf9Zr8O8LLhnjL-2");
        } catch (BaseException e) {
            e.printStackTrace();
        }
        assertThat(res).isEqualTo(38);
    }
    @Test
    @DisplayName("FCM 토큰 존재 X 테스트")
    public void checkFcmTokenIdNotExist() {
//        assertThrows(EmptyResultDataAccessException.class, () -> userProvider.checkFcmTokenExist("notExistFcmToken"));
        Long res = Long.valueOf(0);
        try {
            res = userProvider.checkFcmTokenExist("notExistFcmToken");
            Assertions.assertThat(res).isEqualTo(0);
        } catch (BaseException e) {
            e.printStackTrace();
        }

    }
}