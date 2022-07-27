package com.todoary.ms.src.alarm;

import com.todoary.ms.src.alarm.model.Alarm;
import com.todoary.ms.util.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.todoary.ms.util.BaseResponseStatus.*;

@Slf4j
@Service
public class AlarmService {
    private final AlarmDao alarmDao;

    public AlarmService(AlarmDao alarmDao) {
        this.alarmDao = alarmDao;
    }

    @Transactional
    public void createAlarmTodo(Long userId,Long todoId) throws BaseException {

        try {
            alarmDao.insertAlarmTodo(userId,todoId);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    @Transactional
    public void modifyAlarmTodo(Long userId,Long todoId) throws BaseException {

        try {
            alarmDao.updateAlarmTodo(userId,todoId);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }


}
