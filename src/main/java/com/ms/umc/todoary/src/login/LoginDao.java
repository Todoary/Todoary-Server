package com.ms.umc.todoary.src.login;

import com.ms.umc.todoary.src.login.model.PostLoginReq;
import com.ms.umc.todoary.src.login.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class LoginDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public User selectUser(PostLoginReq postLoginReq) {
        String getUserQuery = "select id, name, nickname, email, password from user where email = ?";
        String getUserParam = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(
                getUserQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getString("password")),
                getUserParam);
    }

}
