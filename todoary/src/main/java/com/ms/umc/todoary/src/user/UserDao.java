package com.ms.umc.todoary.src.user;

import com.ms.umc.todoary.config.BaseException;
import com.ms.umc.todoary.src.user.model.PostUserReq;
import com.ms.umc.todoary.src.user.model.PostUserRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public PostUserRes insertUser(PostUserReq postUserReq) {
        String insertUserQuery = "insert into User(name, nickName, email, password)" +
                "values(?,?,?,?);";
        Object[] insertUserParam = new Object[]{postUserReq.getName(), postUserReq.getNickName(), postUserReq.getEmail(), postUserReq.getPassword()};
        PostUserRes postUserRes = new PostUserRes(postUserReq.getNickName(), postUserReq.getEmail());

        int result = this.jdbcTemplate.update(insertUserQuery, insertUserParam);

        if (result == 1)
            return postUserRes;
        else
            return null;
    }

    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        return this.jdbcTemplate.queryForObject(checkEmailQuery,int.class,email);
    }
}
