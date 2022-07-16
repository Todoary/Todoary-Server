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
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from user where email = ?)";
        String checkEmailParam = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery, int.class, checkEmailParam);
    }

    public int checkName(String name) {
        String checkNameQuery = "select exists(select name from user where name = ?)";
        String checkNameParam = name;
        return this.jdbcTemplate.queryForObject(checkNameQuery, int.class, checkNameParam);
    }

    public int checkId(int id) {
        String checkNameQuery = "select exists(select id from user where id = ?)";
        int checkIdParam = id;
        return this.jdbcTemplate.queryForObject(checkNameQuery, int.class, checkIdParam);
    }

    public User selectUserById(int id) {
        String findByIdQuery = "select id,email, password,nickname,introduce from user where id=?";
        int findByIdParams = id;
        return this.jdbcTemplate.queryForObject(findByIdQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("nickname"),
                        rs.getString("introduce")),
                findByIdParams);
    }

    public User selectUserByEmail(String email) {
        String findByEmailQuery = "select id,email, password,nickname,introduce from user where email=?";
        String findByEmailParams = email;
        return this.jdbcTemplate.queryForObject(findByEmailQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("nickname"),
                        rs.getString("introduce")),
                findByEmailParams);
    }

    public int insertUser(PostUserReq postUserReq) {
        String insertUserQuery = "insert into user(name, nickname, email, password, provider, provider_id)" +
                "values(?,?,?,?, ?, ?);";
        Object[] insertUserParam = new Object[]{postUserReq.getName(), postUserReq.getNickname(), postUserReq.getEmail(), postUserReq.getPassword(),
                postUserReq.getProvider(), postUserReq.getProviderId()};

        this.jdbcTemplate.update(insertUserQuery, insertUserParam);

        String lastInsertIdxQuery = "SELECT MAX(id) FROM user";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);
    }

}
