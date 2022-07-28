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
                +postAlarmReq.getTitle() + " " + postAlarmReq.getBody());

        firebaseCloudMessageService.sendMessageTo(
                postAlarmReq.getTargetToken(),
                postAlarmReq.getTitle(),
                postAlarmReq.getBody());
        return ResponseEntity.ok().build();
    }

    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void test() throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String now = dateFormat.format(new Date());

        String[] target_datetime = now.split(" ");
        String target_date = target_datetime[0];
        String target_time = target_datetime[1];

        List<Alarm> alarmsPerMin = alarmDao.selectByDateTime(target_date, target_time);
        for (Alarm alarm : alarmsPerMin) {
            firebaseCloudMessageService.sendMessageTo(
                    alarm.getRegistration_token(),
                    "Todoary 알림",
                    alarm.getTitle());
        }
    }
}
