package com.todoary.ms.src.service.alarm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.todoary.ms.src.common.exception.TodoaryException;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.service.todo.TodoService;
import com.todoary.ms.src.web.dto.alarm.FcmMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Predicate;

import static com.todoary.ms.src.common.response.BaseResponseStatus.FCM_MESSAGE_PARSING_FAILURE;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class FireBaseCloudMessageService {
    @Value("${fcm.authorization.config}")
    private String FCM_CONFIG_FILE_PATH;

    @Value("${fcm.message-send-url}")
    private String MESSAGE_SEND_URL;

    @Value("${fcm.authorization.get-auth-url}")
    private String FCM_GETTING_AUTHORIZATION_URL;

    private final ObjectMapper objectMapper;
    private final TodoService todoService;

    public void sendMessageTo(String fcm_token, String title, String body) { // targetToken : 에 해당하는 device로 보낼 것이다.
        if (fcm_token == null || fcm_token.isEmpty() || fcm_token.isBlank()) {
            return;
        }
        try {
            String message = makeMessage(fcm_token, title, body);

            OkHttpClient client = new OkHttpClient(); // okhttp3를 이용해 Http post request 생성
            RequestBody requestBody = RequestBody.create(message,
                                                         MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(MESSAGE_SEND_URL)
                    .post(requestBody)
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken()) // Authorization 헤더에 access token 추가
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .build();
            Response response = client.newCall(request).execute();

            log.info(response.body().string());
        } catch (IOException exception) {
            throw new TodoaryException(FCM_MESSAGE_PARSING_FAILURE);
        }
    }

    private String makeMessage(String fcm_token, String title, String body) {
        try {
            FcmMessage fcmMessage =
                    FcmMessage.builder()
                            .message(FcmMessage.Message
                                             .builder()
                                             .token(fcm_token)
                                             .notification(FcmMessage.Notification
                                                                   .builder()
                                                                   .title(title)
                                                                   .body(body)
                                                                   .image(null)
                                                                   .build())
                                             .build())
                            .validateOnly(false)
                            .build();
            return objectMapper.writeValueAsString(fcmMessage);
        } catch (JsonProcessingException e) {
            throw new TodoaryException(FCM_MESSAGE_PARSING_FAILURE);
        }
    }

    private String getAccessToken() {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials // GoogleCredentials : Google API를 사용하기 위해 oauth2를 이용해 인증한 대상
                    .fromStream(new ClassPathResource(FCM_CONFIG_FILE_PATH).getInputStream()) // firebase/firebase_service_key.json를 inputstream으로 가져옴
                    .createScoped(List.of(FCM_GETTING_AUTHORIZATION_URL)); // 서버에서 필요로하는 권한 설정

            googleCredentials.refreshIfExpired(); //설정이 적용된 객체로부터 access token 생성
            return googleCredentials.getAccessToken().getTokenValue(); // access token 값을 가져옴 >> rest api를 통해 fcm에 push 요청을 할 때 header에 담아서 인증할 것임.
        } catch (IOException exception) {
            throw new TodoaryException(FCM_MESSAGE_PARSING_FAILURE);
        }
    }

    public void sendDailyAlarm(List<Member> dailyAlarmEnabledMembers) {
        dailyAlarmEnabledMembers.stream()
                .filter(member -> canMemberReceiveAlarm(member, Member::getDailyAlarmEnable))
                .map(member -> member.getFcmToken().getCode())
                .forEach(fcmToken -> sendMessageTo(
                        fcmToken,
                        "하루기록 알림",
                        "하루기록을 작성해보세요.")
                );
    }

    public void sendTodoAlarm(LocalDate targetDate, LocalTime targetTime) {
        todoService.findAllByDateTime(targetDate, targetTime).stream()
                .filter(todo -> canMemberReceiveAlarm(todo.getMember(), Member::getToDoAlarmEnable))
                .forEach(todo -> {
                    String todoTitle = todo.getTitle();
                    String fcmToken = todo.getMember().getFcmToken().getCode();
                    sendMessageTo(
                            fcmToken,
                            "Todoary 알림",
                            todoTitle
                    );
                });
    }

    public void sendRemindAlarm(List<Member> targetMembers) {
        targetMembers.stream()
                .filter(member -> canMemberReceiveAlarm(member, Member::getRemindAlarmEnable))
                .map(member -> member.getFcmToken().getCode())
                .forEach(fcmToken -> sendMessageTo(
                        fcmToken,
                        "리마인드 알림",
                        "하루기록을 작성한 지 일주일이 경과했습니다.")
                );
    }

    private boolean canMemberReceiveAlarm(Member member, Predicate<Member> alarmEnabled) {
        return !member.isDeleted() && member.getFcmToken() != null && isCodeValid(member.getFcmToken().getCode()) && alarmEnabled.test(member);
    }

    private boolean isCodeValid(String code) {
        return code != null && !code.isEmpty() && !code.isBlank();
    }

}
