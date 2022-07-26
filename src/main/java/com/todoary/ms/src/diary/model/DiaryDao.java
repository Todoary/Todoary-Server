package com.todoary.ms.src.diary.model;


import com.todoary.ms.src.diary.dto.GetDiaryByDateRes;
import com.todoary.ms.src.diary.dto.PostDiaryReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
public class DiaryDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DiaryDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Transactional
    public long insertDiary(long userId, String title, String targetDate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String insertDiaryQuery = "INSERT INTO diary (user_id, title, target_date) VALUES(?, ?, ?);";
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement pstmt = con.prepareStatement(insertDiaryQuery,
                        new String[]{"id"});
                pstmt.setLong(1, userId);
                pstmt.setString(2, title);
                pstmt.setString(3, targetDate);
                return pstmt;
            }
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public int selectExistsUsersDiaryById(long userId, long diaryId) {
        String selectExistsUsersDiaryByIdQuery = "SELECT EXISTS(SELECT user_id, id FROM diary " +
                "where user_id = ? and id = ?)";
        Object[] selectExistsUsersDiaryByIdParams = new Object[]{userId, diaryId};
        return this.jdbcTemplate.queryForObject(selectExistsUsersDiaryByIdQuery, int.class, selectExistsUsersDiaryByIdParams);
    }

    public void updateDiary(long userId,long diaryId, PostDiaryReq postDiaryReq) {
        String updateDiaryQuery = "UPDATE diary " +
                "SET title = ?, target_date = ?" +
                "WHERE id = ?";
        Object[] updateDiaryParams = new Object[]{postDiaryReq.getTitle(), postDiaryReq.getTargetDate(), diaryId};
        this.jdbcTemplate.update(updateDiaryQuery, updateDiaryParams);
    }

    public void deleteDiary(long diaryId) {
        String deleteDiaryQuery = "DELETE FROM diary WHERE id = ?";
        long deleteDiaryParam = diaryId;
        this.jdbcTemplate.update(deleteDiaryQuery, deleteDiaryParam);
    }

    public List<GetDiaryByDateRes> selectDiaryListByDate(long userId, String targetDate) {
        String selectDiaryByDateQuery =



    }

}
