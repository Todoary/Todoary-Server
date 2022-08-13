package com.todoary.ms.src.alarm;

import com.todoary.ms.src.alarm.dto.PostAlarmReq;
import com.todoary.ms.src.alarm.model.Alarm;
import com.todoary.ms.util.ErrorLogWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/alarm")
public class AlarmController {

    private final FireBaseCloudMessageService firebaseCloudMessageService;
    private final AlarmDao alarmDao;

    @Autowired
    public AlarmController(FireBaseCloudMessageService firebaseCloudMessageService, AlarmDao alarmDao) {
        this.firebaseCloudMessageService = firebaseCloudMessageService;
        this.alarmDao = alarmDao;
    }

    @PostMapping("/todo")
    public ResponseEntity pushMessage(@RequestBody PostAlarmReq postAlarmReq) {
        System.out.println(postAlarmReq.getTargetToken() + " "
                + postAlarmReq.getTitle() + " " + postAlarmReq.getBody());

        try {
            firebaseCloudMessageService.sendMessageTo(
                    postAlarmReq.getTargetToken(),
                    postAlarmReq.getTitle(),
                    postAlarmReq.getBody());
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            ErrorLogWriter.writeExceptionWithMessage(e, "push message failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void TodoaryAlarm() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String now = dateFormat.format(new Date());

        String[] target_datetime = now.split(" ");
        String target_date = target_datetime[0];
        String target_time = target_datetime[1];

        List<Alarm> alarms_todo = alarmDao.selectByDateTime_todo(target_date, target_time);
        for (Alarm alarm : alarms_todo) {
            try {
                firebaseCloudMessageService.sendMessageTo(
                        alarm.getRegistration_token(),
                        "Todoary 알림",
                        alarm.getTitle());
            } catch (IOException e) {
                ErrorLogWriter.writeExceptionWithMessage(e, "Todoary 알림 failed | " + alarm.toString());
                throw new RuntimeException(e);
            }
        }
    }

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void DailyAlarm(){

        List<Alarm> alarms_daily = alarmDao.selectByDateTime_daily();
        for (Alarm alarm : alarms_daily) {
            try {
                firebaseCloudMessageService.sendMessageTo(
                        alarm.getRegistration_token(),
                        "하루기록 알림",
                        "하루기록을 작성해보세요.");
            } catch (IOException e) {
                ErrorLogWriter.writeExceptionWithMessage(e, "하루기록 알림 failed | " + alarm.toString());
                throw new RuntimeException(e);
            }
        }

    }

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void RemindAlarm() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String target_date = dateFormat.format(new Date());

        List<Alarm> alarms_remind = alarmDao.selectByDateTime_remind(target_date);
        for (Alarm alarm : alarms_remind) {
            try {
                firebaseCloudMessageService.sendMessageTo(
                        alarm.getRegistration_token(),
                        "리마인드 알림",
                        "하루기록을 작성한 지 일주일이 경과했습니다.");
            } catch (IOException e) {
                ErrorLogWriter.writeExceptionWithMessage(e, "리마인드 알림 failed | " + alarm.toString());
                throw new RuntimeException(e);
            }
        }
    }

    @Scheduled(cron = "0 0 ${fcm.secret.time} ? * ${fcm.secret.day}")
    public void AlarmRemove(){
        try {
            alarmDao.deleteByDateTime_todo();
        } catch (Exception e) {
            ErrorLogWriter.writeExceptionWithMessage(e, "알람 삭제 failed");
            e.printStackTrace();
        }
    }

}

