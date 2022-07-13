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

    // login
    public User getPwd(PostLoginReq postLoginReq) {
        System.out.print("hi");
        String getPwdQuery = "select id, name, nickName,  email, password from User where email = ?";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email"),
                        rs.getString("password")),
                getPwdParams);

    }

    // user check
    public int checkUserExist(int userIdx) {
        String checkUserExistQuery = "select exists(select id from User where id = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);

    }

    // email check
    public int checkEmailExist(String email) {
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

}
