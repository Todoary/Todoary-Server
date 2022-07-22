package com.todoary.ms.src.user;

import com.todoary.ms.src.user.dto.*;
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
        Object[] insertUserParams = new Object[]{user.getUsername(), user.getNickname(), user.getEmail(), user.getPassword(), user.getRole(), user.getProvider(), user.getProvider_id()};

        this.jdbcTemplate.update(insertUserQuery, insertUserParams);

        Long lastInsertId = this.jdbcTemplate.queryForObject("select last_insert_id()", Long.class);

        user.setId(lastInsertId);

        return user;
    }

    public User selectByEmail(String email, String provider) {
        String selectByEmailQuery = "select id, username, nickname,email, password,profile_img_url, introduce, role, provider, provider_id from user where email = ? and provider = ? and status = 1";
        Object[] selectByEmailParams = new Object[]{email, provider};
        try {
            return this.jdbcTemplate.queryForObject(selectByEmailQuery,
                    (rs, rowNum) -> new User(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("nickname"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("profile_img_url"),
                            rs.getString("introduce"),
                            rs.getString("role"),
                            rs.getString("provider"),
                            rs.getString("provider_id")),
                    selectByEmailParams);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public User selectById(Long user_id) {
        String selectByIdQuery = "select id, username,nickname,email,password,profile_img_url,introduce,role, provider, provider_id from user where id = ? and status = 1";
        return this.jdbcTemplate.queryForObject(selectByIdQuery,
                (rs, rowNum) -> new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("profile_img_url"),
                        rs.getString("introduce"),
                        rs.getString("role"),
                        rs.getString("provider"),
                        rs.getString("provider_id")),
                user_id);
    }

    public int checkEmail(String email, String provider) {
        String checkEmailQuery = "select exists(select email, provider from user where email = ? and provider = ? and status = 1)";
        Object[] checkEmailParams = new Object[]{email, provider};
        return this.jdbcTemplate.queryForObject(checkEmailQuery, int.class, checkEmailParams);
    }

    public int checkNickname(String nickname) {
        String checkNameQuery = "select exists(select nickname from user where nickname = ? and status = 1)";
        String checkNameParam = nickname;
        return this.jdbcTemplate.queryForObject(checkNameQuery, int.class, checkNameParam);
    }

    public int checkId(Long id) {
        String checkIdQuery = "select exists(select nickname from user where id = ? and status = 1)";
        Long checkIdParam = id;
        return this.jdbcTemplate.queryForObject(checkIdQuery, int.class, checkIdParam);
    }

    public String updateProfileImg(Long user_id, String profile_img_url) {
        String updateProfileImgQuery = "update user set profile_img_url = ? where id = ?";
        Object[] updateProfileImgParams = new Object[]{profile_img_url, user_id};

        this.jdbcTemplate.update(updateProfileImgQuery, updateProfileImgParams);

        return profile_img_url;
    }

    public PatchUserRes updateProfile(Long user_id, PatchUserReq patchUserReq) {
        String updateProfileQuery = "update user set nickname = ? , introduce = ? where id = ? and status = 1";
        Object[] updateProfileParams = new Object[]{patchUserReq.getNickname(), patchUserReq.getIntroduce(), user_id};

        int result = this.jdbcTemplate.update(updateProfileQuery, updateProfileParams);
        if (result == 1)
            return new PatchUserRes(patchUserReq.getNickname(), patchUserReq.getIntroduce());
        else
            return null;
    }

    public void updateUserStatus(Long user_id) {
        String updateStatusQuery = "update user set status = 0 where id = ?";
        Long updateStatusParam = user_id;
        this.jdbcTemplate.update(updateStatusQuery, updateStatusParam);
    }

    public void todoAlarmStatus(Long user_id, PatchTodoaryAlarmReq patchTodoaryAlarmReq) {
        String alarmStatusQuery = "update user set alarm_todo = 1 where id = ?";
        Long alarmStatusParam = user_id;
        this.jdbcTemplate.update(alarmStatusQuery, alarmStatusParam);
    }

    public void diaryAlarmStatus(Long user_id, PatchDiaryAlarmReq patchDiaryAlarmReq) {
        String alarmStatusQuery = "update user set alarm_diary = 1 where id = ?";
        Long alarmStatusParam = user_id;
        this.jdbcTemplate.update(alarmStatusQuery, alarmStatusParam);
    }

    public void remindAlarmStatus(Long user_id, PatchRemindAlarmReq patchRemindAlarmReq) {
        String alarmStatusQuery = "update user set alarm_remind = 1 where id = ?";
        Long alarmStatusParam = user_id;
        this.jdbcTemplate.update(alarmStatusQuery, alarmStatusParam);
    }

    public void termsStatus(Long user_id, PatchTermsReq patchTermsReq) {
        String termsStatusQuery = "update user set terms = 1 where id = ?";
        Long termsStatusParam = user_id;
        this.jdbcTemplate.update(termsStatusQuery, termsStatusParam);
    }
}
