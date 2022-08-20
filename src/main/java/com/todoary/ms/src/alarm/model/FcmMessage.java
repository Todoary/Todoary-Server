package com.todoary.ms.src.alarm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FcmMessage { // Request를 보낼 때 FCM에서 정해준 형식에 따라 보내야한다. https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages/send 참고
    // 이 클래스를 통해 생성된 객체는 Object Mapper를 통해 String으로 변환되어, Http Post요청 Request Body에 담겨 전송될 것이다.
    private boolean validateOnly; //실제로 메시지를 전달하지 않고 요청을 테스트하기 위한 플래그
    private Message message; // 필수의. 보낼 메시지입니다.

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private Notification notification; // 모든 mobile os 를 아우를 수 있다
        private String token; // 특정 device에 알림을 보내기 위해 사용 (token / topic / condition 으로 구분 무조건 하나만 선택해야함.)
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;// 알림의 제목
        private String body;// 알림의 본문
        private String image;
    }
}