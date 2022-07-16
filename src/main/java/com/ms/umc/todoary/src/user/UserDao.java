package com.ms.umc.todoary.src.user;

import com.ms.umc.todoary.src.auth.model.PostUserReq;
import com.ms.umc.todoary.src.entity.User;
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

    public int insertUser(PostUserReq postUserReq) {
        String insertUserQuery = "insert into user(name, nickname, email, password)" +
                "values(?,?,?,?);";
        Object[] insertUserParam = new Object[]{postUserReq.getName(), postUserReq.getNickname(), postUserReq.getEmail(), postUserReq.getPassword()};
        
        this.jdbcTemplate.update(insertUserQuery, insertUserParam);

        String lastInsertIdxQuery="SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);
    }

    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from user where email = ?)";
        return this.jdbcTemplate.queryForObject(checkEmailQuery,int.class,email);
    }

    public int checkName(String name) {
        String checkNameQuery = "select exists(select email from user where name = ?)";
        return this.jdbcTemplate.queryForObject(checkNameQuery,int.class,name);
    }

    public User findById(int id) {
        String findByIdQuery = "select id,name, nickname,email,password from user where id=?";
        int findByIdParams = id;
        return this.jdbcTemplate.queryForObject(findByIdQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getString("password")),
                findByIdParams);
    }

    public User findByEmail(String email) {
        String findByEmailQuery = "select id,name, nickname,email,password from user where email=?";
        String findByEmailParams = email;
        return this.jdbcTemplate.queryForObject(findByEmailQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getString("password")),
                findByEmailParams);
    }


}
