package com.todoary.ms.src.alarm;

import com.todoary.ms.src.alarm.dto.PostAlarmReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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

    // @Scheduled(cron = "0 0/1 * 1/1 * ?")
    // public void test() throws IOException {
    //     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    //     String now = dateFormat.format(new Date());
    //
    //     List<Alarm> alarmsPerMin = alarmDao.selectByDateTime(now);
    //     for (Alarm alarm : alarmsPerMin) {
    //         firebaseCloudMessageService.sendMessageTo(
    //                 alarm.getRegistration_token(),
    //                 alarm.getTitle(),
    //                 alarm.getTarget_date());
    //     }
    // }
}
