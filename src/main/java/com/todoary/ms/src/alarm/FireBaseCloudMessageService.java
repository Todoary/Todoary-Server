package com.todoary.ms.src.alarm;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.todoary.ms.src.alarm.model.FcmMessage;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FireBaseCloudMessageService {

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/todoary-1304d/messages:send";
    private final ObjectMapper objectMapper;

    public void sendMessageTo(String fcm_token, String title, String body) throws IOException { // targetToken : 에 해당하는 device로 보낼 것이다.
        String message = makeMessage(fcm_token, title, body);

        OkHttpClient client = new OkHttpClient(); // okhttp3를 이용해 Http post request 생성
        RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken()) // Authorization 헤더에 access token 추가
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonParseException, JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .fcm_token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        ).build()).validateOnly(false).build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/firebase_service_key.json";

        GoogleCredentials googleCredentials = GoogleCredentials // GoogleCredentials : Google API를 사용하기 위해 oauth2를 이용해 인증한 대상
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream()) // firebase/firebase_service_key.json를 inputstream으로 가져옴
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform")); // 서버에서 필요로하는 권한 설정

        googleCredentials.refreshIfExpired(); //설정이 적용된 객체로부터 access token 생성
        return googleCredentials.getAccessToken().getTokenValue(); // access token 값을 가져옴 >> rest api를 통해 fcm에 push 요청을 할 때 header에 담아서 인증할 것임.
    }
}
