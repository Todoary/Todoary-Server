package com.todoary.ms.src.alarm;

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

//    public List<Alarm> selectByDateTime(String dateTime) {
//        String selectByDateTimeQuery = "select * from alarm where alarm_datetime like ?";
//        dateTime += "%";
//        return this.jdbcTemplate.query(selectByDateTimeQuery,
//                (rs, rowNum) -> new Alarm(
//                        rs.getLong("user_id"),
//                        rs.getString("registration_token"),
//                        rs.getString("title"),
//                        rs.getString("body"),
//                        rs.getDate("alarm_datetime" +
//                                "")
//                ), dateTime);
//    }
}