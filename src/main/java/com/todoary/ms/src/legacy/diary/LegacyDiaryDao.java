package com.todoary.ms.src.legacy.diary;


import com.todoary.ms.src.legacy.diary.dto.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


@Repository
public class LegacyDiaryDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LegacyDiaryDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void insertOrUpdateDiary(long userId, PostDiaryReq postDiaryReq, String createdDate) {
        String insertDiaryQuery = "INSERT INTO diary (user_id, title, content, created_date) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE title=?, content=?";
        Object[] insertDiaryParams = new Object[]{userId, postDiaryReq.getTitle(), postDiaryReq.getContent(), createdDate, postDiaryReq.getTitle(), postDiaryReq.getContent()};
        this.jdbcTemplate.update(insertDiaryQuery, insertDiaryParams);
    }


    public int selectExistsUsersDiaryByDate(Long userId, String createdDate) {
        String selectExistsUsersDiaryByIdQuery = "SELECT EXISTS(SELECT user_id, id FROM diary " +
                "WHERE user_id = ? and created_date = ?)";
        Object[] selectExistsUsersDiaryByIdParams = new Object[]{userId, createdDate};
        return this.jdbcTemplate.queryForObject(selectExistsUsersDiaryByIdQuery, int.class, selectExistsUsersDiaryByIdParams);
    }

    public void deleteDiary(Long diaryId) {
        String deleteDiaryQuery = "DELETE FROM diary WHERE id = ?";
        Long deleteDiaryParam = diaryId;
        this.jdbcTemplate.update(deleteDiaryQuery, deleteDiaryParam);
    }


    public GetDiaryByDateRes selectDiaryByDate(Long diaryId) {
        String selectDiaryByDateQuery = "SELECT id, title, content, created_date " +
                " FROM diary WHERE id = ?";
        Long selectDiaryByDateParam = diaryId;
        return this.jdbcTemplate.queryForObject(selectDiaryByDateQuery,
                                                (rs, rowNum) -> new GetDiaryByDateRes(
                                                        rs.getLong("id"),
                                                        rs.getString("title"),
                                                        rs.getString("content"),
                                                        rs.getString("created_date")
                                                ), selectDiaryByDateParam);
    }

    public List<Integer> selectIsDiaryInMonth(Long userId, String yearAndMonth) {
        String selectIsDiaryInMonthQuery = "SELECT DAY(created_date) as day " +
                "FROM diary WHERE user_id=? and ? = DATE_FORMAT(created_date, '%Y-%m') " +
                "GROUP by day ORDER BY day";
        Object[] selectIsDiaryInMonthParams = new Object[]{userId, yearAndMonth};
        return this.jdbcTemplate.query(selectIsDiaryInMonthQuery,
                                       (rs, rowNum) -> (rs.getInt("day")), selectIsDiaryInMonthParams);
    }

    public Long selectDiaryIdByDate(Long userId, String createdDate) {
        String selectDiaryIdByDateQuery = "SELECT id FROM diary WHERE user_id = ? and created_date=?";
        Object[] selectDiaryIdByDateParams = new Object[]{userId, createdDate};
        return this.jdbcTemplate.queryForObject(selectDiaryIdByDateQuery,
                                                long.class,
                                                selectDiaryIdByDateParams);
    }

    /**
     * 새로 추가된 스티커의 generate된 id 반환
     *
     * @param diaryId
     * @param createdSticker
     * @return
     */
    public Long insertSticker(Long diaryId, CreateStickerReq createdSticker) {
        String insertStickerQuery = "INSERT INTO diary_sticker(diary_id,sticker_id,locationX,locationY,width,height, rotation, flipped) VALUES(?, ?,?, ?, ?,?,?,?) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            @NotNull
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement pstmt = con.prepareStatement(insertStickerQuery,
                                                               new String[]{"id"});
                pstmt.setLong(1, diaryId);
                pstmt.setLong(2, createdSticker.getStickerId());
                pstmt.setDouble(3, createdSticker.getLocationX());
                pstmt.setDouble(4, createdSticker.getLocationY());
                pstmt.setDouble(5, createdSticker.getWidth());
                pstmt.setDouble(6, createdSticker.getHeight());
                pstmt.setDouble(7, createdSticker.getRotation());
                pstmt.setBoolean(8, createdSticker.isFlipped());
                return pstmt;
            }
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }


    public void updateStickers(List<ModifyStickerReq> modifiedStickers) {
        String updateStickerQuery = "update diary_sticker set locationX=?, locationY=?, width=?, height=?, rotation=?, flipped=? where id=?";
        this.jdbcTemplate.batchUpdate(updateStickerQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                ps.setDouble(1, modifiedStickers.get(i).getLocationX());
                ps.setDouble(2, modifiedStickers.get(i).getLocationY());
                ps.setDouble(3, modifiedStickers.get(i).getWidth());
                ps.setDouble(4, modifiedStickers.get(i).getHeight());
                ps.setDouble(5, modifiedStickers.get(i).getRotation());
                ps.setBoolean(6, modifiedStickers.get(i).isFlipped());
                ps.setLong(7, modifiedStickers.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return modifiedStickers.size();
            }
        });
    }

    public void deleteStickers(List<DeleteStickerReq> deletedStickers) {
        String deleteStickerQuery = "DELETE FROM diary_sticker WHERE id=?";
        this.jdbcTemplate.batchUpdate(deleteStickerQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, deletedStickers.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return deletedStickers.size();
            }
        });
    }

    public List<GetStickerRes> selectStickerListByDate(Long diaryId) {
        String selectStickerByDateQuery = "SELECT id, diary_id as diaryId, sticker_id as stickerId ,locationX,locationY, width, height, rotation, flipped, created_date " +
                "FROM diary_sticker " +
                "WHERE diary_id = ? ";
        Object[] selectStickerListByDateParam = new Object[]{diaryId};
        return this.jdbcTemplate.query(selectStickerByDateQuery,
                                       (rs, rowNum) -> new GetStickerRes(
                                               rs.getLong("id"),
                                               rs.getLong("diaryId"),
                                               rs.getLong("stickerId"),
                                               rs.getDouble("locationX"),
                                               rs.getDouble("locationY"),
                                               rs.getDouble("width"),
                                               rs.getDouble("height"),
                                               rs.getDouble("rotation"),
                                               rs.getBoolean("flipped"),
                                               rs.getString("created_date")
                                       ), selectStickerListByDateParam);
    }


    public void deleteSticker(Long id) {
        String deleteStickerQuery = "DELETE FROM diary_sticker WHERE id=?";
        Long deleteStickerParam = id;
        this.jdbcTemplate.update(deleteStickerQuery, deleteStickerParam);
    }


}
