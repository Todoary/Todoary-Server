package com.todoary.ms.src.legacy.alarm;

import com.todoary.ms.src.legacy.alarm.dto.PostAlarmReq;
import com.todoary.ms.src.legacy.alarm.model.Alarm;
import com.todoary.ms.src.common.util.ErrorLogWriter;
import com.todoary.ms.src.service.alarm.FireBaseCloudMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//@RestController
@RequestMapping("/alarm")
public class LegacyAlarmController {

    private final FireBaseCloudMessageService firebaseCloudMessageService;
    private final LegacyAlarmDao legacyAlarmDao;

    @Autowired
    public LegacyAlarmController(FireBaseCloudMessageService firebaseCloudMessageService, LegacyAlarmDao legacyAlarmDao) {
        this.firebaseCloudMessageService = firebaseCloudMessageService;
        this.legacyAlarmDao = legacyAlarmDao;
    }

    @PostMapping("/todo")
    public ResponseEntity pushMessage(@RequestBody PostAlarmReq postAlarmReq) {
        System.out.println(postAlarmReq.getFcm_token() + " "
                + postAlarmReq.getTitle() + " " + postAlarmReq.getBody());

        firebaseCloudMessageService.sendMessageTo(
                postAlarmReq.getFcm_token(),
                postAlarmReq.getTitle(),
                postAlarmReq.getBody());
        return ResponseEntity.ok().build();
    }

    //@Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void TodoaryAlarm() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String now = dateFormat.format(new Date());

        String[] target_datetime = now.split(" ");
        String target_date = target_datetime[0];
        String target_time = target_datetime[1];

        List<Alarm> alarms_todo = legacyAlarmDao.selectByDateTime_todo(target_date, target_time);
        for (Alarm alarm : alarms_todo) {
            firebaseCloudMessageService.sendMessageTo(
                    alarm.getFcm_token(),
                    "Todoary 알림",
                    alarm.getTitle());
        }
    }

    //@Scheduled(cron = "0 0 0 1/1 * ?")
    public void DailyAlarm(){

        List<Alarm> alarms_daily = legacyAlarmDao.selectByDateTime_daily();
        for (Alarm alarm : alarms_daily) {
            firebaseCloudMessageService.sendMessageTo(
                    alarm.getFcm_token(),
                    "하루기록 알림",
                    "하루기록을 작성해보세요.");
        }

    }

    //@Scheduled(cron = "0 0 0 1/1 * ?")
    public void RemindAlarm() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String target_date = dateFormat.format(new Date());

        List<Alarm> alarms_remind = legacyAlarmDao.selectByDateTime_remind(target_date);
        for (Alarm alarm : alarms_remind) {
            firebaseCloudMessageService.sendMessageTo(
                    alarm.getFcm_token(),
                    "리마인드 알림",
                    "하루기록을 작성한 지 일주일이 경과했습니다.");
        }
    }

    //@Scheduled(cron = "0 0 ${fcm.secret.time} ? * ${fcm.secret.day}")
    public void AlarmRemove(){
        try {
            legacyAlarmDao.deleteByDateTime_todo();
        } catch (Exception e) {
            ErrorLogWriter.writeExceptionWithMessage(e, "알람 삭제 failed");
            e.printStackTrace();
        }
    }

}

