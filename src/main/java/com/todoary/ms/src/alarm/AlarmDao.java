package com.todoary.ms.src.alarm;

import com.todoary.ms.src.alarm.model.Alarm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AlarmDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AlarmDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insertAlarmTodo(Long userId,Long todoId) {
        String insertAlarmQuery = "insert into alarm_todo (user_id, todo_id) values (?,?)";
        Object[] insertAlarmParams = new Object[]{userId,todoId};
        this.jdbcTemplate.update(insertAlarmQuery, insertAlarmParams);

    }
    public void updateAlarmTodo(Long userId,Long todoId) {
        String insertAlarmQuery = "update alarm_todo set status = 0 where user_id = ? and todo_id = ?";
        Object[] insertAlarmParams = new Object[]{userId,todoId};
        this.jdbcTemplate.update(insertAlarmQuery, insertAlarmParams);

    }

    public List<Alarm> selectByDateTime(String dateTime) {
        String selectByDateTimeQuery = "select * from alarm where alarm_datetime like ?";
        dateTime += "%";
        return this.jdbcTemplate.query(selectByDateTimeQuery,
                (rs, rowNum) -> new Alarm(
                        rs.getLong("user_id"),
                        rs.getString("registration_token"),
                        rs.getString("title"),
                        rs.getString("body"),
                        rs.getDate("alarm_datetime" +
                                "")
                ), dateTime);
    }
}