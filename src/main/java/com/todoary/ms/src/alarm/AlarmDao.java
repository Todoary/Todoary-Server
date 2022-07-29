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

    public List<Alarm> selectByDateTime_todo(String target_date, String target_time) {
        String selectByDateTime_todoQuery = "select registration_token, title, target_date,target_time\n" +
                "        from alarm_todo a\n" +
                "        join (select id, title, target_date,target_time from todo where target_date = ? and target_time like ? and is_checked = 0) t on a.todo_id = t.id\n" +
                "        join (select id, registration_token from user where status = 1 and alarm_todo = 1) u on a.user_id = u.id;";

        target_time += "%";

        Object[] selectByDateTime_todoParams = new Object[]{target_date, target_time};
        return this.jdbcTemplate.query(selectByDateTime_todoQuery,
                (rs, rowNum) -> new Alarm(
                        rs.getString("registration_token"),
                        rs.getString("title"),
                        rs.getDate("target_date").toString(),
                        rs.getTime("target_time").toString()
                ), selectByDateTime_todoParams);
    }

    public List<Alarm> selectByDateTime_daily() {
        String selectByDateTime_dailyQuery = "select user.registration_token from user\n" +
                "where user.alarm_diary = 1";
        return this.jdbcTemplate.query(selectByDateTime_dailyQuery,
                (rs, rowNum) -> new Alarm(
                        rs.getString("registration_token")
                        )
                );
    }

    public List<Alarm> selectByDateTime_remind(String target_date) {
        String selectByDateTime_remindQuery = "select registration_token, target_date\n" +
                "from (select user_id, target_date from alarm_remind where target_date = ?) a\n" +
                "    join (select id , registration_token from user where status = 1 and alarm_remind = 1) u on u.id = a.user_id";

        return this.jdbcTemplate.query(selectByDateTime_remindQuery,
                (rs, rowNum) -> new Alarm(
                        rs.getString("registration_token"),
                        rs.getString("title"),
                        rs.getDate("target_date").toString(),
                        "00:00:00"
                ), target_date);
    }
}