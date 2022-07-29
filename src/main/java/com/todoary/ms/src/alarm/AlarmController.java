package com.todoary.ms.src.alarm;

import com.todoary.ms.src.alarm.dto.PostAlarmReq;
import com.todoary.ms.src.alarm.model.Alarm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity pushMessage(@RequestBody PostAlarmReq postAlarmReq) throws IOException {
        System.out.println(postAlarmReq.getTargetToken() + " "
                + postAlarmReq.getTitle() + " " + postAlarmReq.getBody());

        firebaseCloudMessageService.sendMessageTo(
                postAlarmReq.getTargetToken(),
                postAlarmReq.getTitle(),
                postAlarmReq.getBody());
        return ResponseEntity.ok().build();
    }

    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void TodoaryAlarm() throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String now = dateFormat.format(new Date());

        String[] target_datetime = now.split(" ");
        String target_date = target_datetime[0];
        String target_time = target_datetime[1];

        List<Alarm> alarms_todo = alarmDao.selectByDateTime_todo(target_date, target_time);
        for (Alarm alarm : alarms_todo) {
            firebaseCloudMessageService.sendMessageTo(
                    alarm.getRegistration_token(),
                    "Todoary 알림",
                    alarm.getTitle());
        }
    }

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void DailyAlarm() throws IOException {

        List<Alarm> alarms_daily =  alarmDao.selectByDateTime_daily();
        for (Alarm alarm : alarms_daily) {
            firebaseCloudMessageService.sendMessageTo(
                    alarm.getRegistration_token(),
                    "하루기록 알림",
                    "하루기록을 작성해보세요.");
        }

    }

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void RemindAlarm() throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String target_date = dateFormat.format(new Date());

        List<Alarm> alarms_remind = alarmDao.selectByDateTime_remind(target_date);
        for (Alarm alarm : alarms_remind) {
            firebaseCloudMessageService.sendMessageTo(
                    alarm.getRegistration_token(),
                    "리마인드 알림",
                    "하루기록을 작성한 지 일주일이 경과했습니다.");
        }
    }
}

