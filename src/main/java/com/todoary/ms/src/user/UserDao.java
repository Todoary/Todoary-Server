package com.todoary.ms.src.user;

import com.todoary.ms.src.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    public User insertUser(User user) {
        String insertUserQuery = "insert into user (username,nickname,email,password,role,provider,provider_id) values (?,?,?,?,?,?,?)";
        Object[] insertUserParams = new Object[]{user.getUsername(), user.getNickname(),user.getEmail(), user.getPassword(), user.getRole(),user.getProvider(),user.getProvider_id()};

        this.jdbcTemplate.update(insertUserQuery, insertUserParams);

        Long lastInsertId = this.jdbcTemplate.queryForObject("select last_insert_id()", Long.class);

        user.setId(lastInsertId);

        return user;
    }

    public User selectByEmail(String email) {
        System.out.println(email);
        String selectByEmailQuery = "select id, username, nickname,email, password, role, provider, provider_id from user where email = ?";

        try {
            return this.jdbcTemplate.queryForObject(selectByEmailQuery,
                    (rs, rowNum) -> new User(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("nickname"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getString("provider"),
                            rs.getString("provider_id")),
                    email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from user where email = ?)";
        return this.jdbcTemplate.queryForObject(checkEmailQuery,int.class,email);
    }
}
