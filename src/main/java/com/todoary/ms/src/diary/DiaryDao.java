package com.todoary.ms.src.diary;


import com.todoary.ms.src.category.dto.PostCategoryReq;
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



    public long insertDiary(long userId, String title, String content) {
        String insertDiaryQuery = "INSERT INTO diary (user_id, title, content) VALUES(?, ?, ?)";
        Object[] insertDiaryParams = new Object[]{userId, title, content};
        this.jdbcTemplate.update(insertDiaryQuery, insertDiaryParams);

        String lastInsertIdQuery = "SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, Long.class);
    }


    public int selectExistsUsersDiaryById(long userId, long diaryId) {
        String selectExistsUsersDiaryByIdQuery = "SELECT EXISTS(SELECT user_id, id FROM diary " +
                "WHERE user_id = ? and id = ?)";
        Object[] selectExistsUsersDiaryByIdParams = new Object[]{userId, diaryId};
        return this.jdbcTemplate.queryForObject(selectExistsUsersDiaryByIdQuery, int.class, selectExistsUsersDiaryByIdParams);
    }

    public void updateDiary(long userId,long diaryId, PostDiaryReq postDiaryReq) {
        String updateDiaryQuery = "UPDATE diary " +
                "SET title = ?, content = ?" +
                "WHERE user_id=? and id = ?";
        Object[] updateDiaryParams = new Object[]{userId, diaryId, postDiaryReq.getTitle(), postDiaryReq.getContent()};
        this.jdbcTemplate.update(updateDiaryQuery, updateDiaryParams);
    }

    public void deleteDiary(long diaryId) {
        String deleteDiaryQuery = "DELETE FROM diary WHERE id = ?";
        long deleteDiaryParam = diaryId;
        this.jdbcTemplate.update(deleteDiaryQuery, deleteDiaryParam);
    }

    public List<GetDiaryByDateRes> selectDiaryListByDate(Long userId, String created_at) {
        String selectDiaryByDateQuery ="SELECT userId, created_at " +
                "FROM diary" +
                "WHERE user_id = ? and id = ? " +
                "ORDER BY created_at";

        return this.jdbcTemplate.query(selectDiaryByDateQuery,
                (rs,rowNum) -> new GetDiaryByDateRes(
                        rs.getLong("diaryId"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("created_at")
                ));

    }

}
