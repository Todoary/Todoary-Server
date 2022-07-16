package com.ms.umc.todoary.src.auth;

import com.ms.umc.todoary.src.auth.model.PostUserReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class AuthDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int insertUser(PostUserReq postUserReq) {
        String insertUserQuery = "insert into user(name, nickname, email, password)" +
                "values(?,?,?,?);";
        Object[] insertUserParam = new Object[]{postUserReq.getName(), postUserReq.getNickname(), postUserReq.getEmail(), postUserReq.getPassword()};

        this.jdbcTemplate.update(insertUserQuery, insertUserParam);

        String lastInsertIdxQuery="SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);
    }
}
