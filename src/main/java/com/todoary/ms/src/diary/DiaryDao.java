package com.todoary.ms.src.diary;


import com.todoary.ms.src.category.dto.GetCategoryRes;
import com.todoary.ms.src.diary.dto.GetDiaryByDateRes;
import com.todoary.ms.src.diary.dto.GetStickerRes;
import com.todoary.ms.src.diary.dto.PostDiaryReq;
import com.todoary.ms.src.diary.dto.PostStickerReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.util.List;


@Repository
public class DiaryDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DiaryDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    public void insertOrUpdateDiary(long userId, PostDiaryReq postDiaryReq, String createdDate) {
        String insertDiaryQuery = "INSERT INTO diary (user_id, title, content, created_date) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE title=?, content=?";
        Object[] insertDiaryParams=new Object[]{userId, postDiaryReq.getTitle(), postDiaryReq.getContent(), createdDate, postDiaryReq.getTitle(), postDiaryReq.getContent()};
        this.jdbcTemplate.update(insertDiaryQuery, insertDiaryParams);
    }


    public int selectExistsUsersDiaryById(Long userId, String createdDate) {
        String selectExistsUsersDiaryByIdQuery = "SELECT EXISTS(SELECT user_id, id FROM diary " +
                "WHERE user_id = ? and created_date = ?)";
        Object[] selectExistsUsersDiaryByIdParams = new Object[]{userId, createdDate};
        return this.jdbcTemplate.queryForObject(selectExistsUsersDiaryByIdQuery, int.class, selectExistsUsersDiaryByIdParams);
    }

    public void deleteDiary(Long userId, String created_date) {
        String deleteDiaryQuery = "DELETE FROM diary "+"WHERE user_id = ? and DATE(?)=DATE(created_date) ";
        Object[] deleteDiaryParam = new Object[]{userId, created_date};
        this.jdbcTemplate.update(deleteDiaryQuery, deleteDiaryParam);
    }



    public GetDiaryByDateRes selectDiaryByDate(Long userId, String created_date) {
        String selectDiaryByDateQuery = "SELECT id, title, content, created_date " +
                "FROM diary " +
                "WHERE user_id = ? and DATE(?)=DATE(created_date) " +
                "ORDER BY created_date ";
        Object[] selectDiaryByDateParams = new Object[]{userId, created_date};
        return this.jdbcTemplate.queryForObject(selectDiaryByDateQuery,
                (rs,rowNum) -> new GetDiaryByDateRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("created_date")
                ),selectDiaryByDateParams);

    }

    public List<Integer> selectIsDiaryInMonth(Long userId, String yearAndMonth) {
        String selectIsDiaryInMonthQuery = "SELECT DAY(created_date) as day " +
                "FROM diary WHERE user_id=? and ? = DATE_FORMAT(created_date, '%Y-%m') " +
                "GROUP by day ORDER BY day";
        Object[] selectIsDiaryInMonthParams = new Object[]{userId, yearAndMonth};
        return this.jdbcTemplate.query(selectIsDiaryInMonthQuery,
                (rs, rowNum) -> (rs.getInt("day")), selectIsDiaryInMonthParams);
    }

    public int selectDiaryIdExist(String created_date){
        String selectDiaryIdExistQuery = "SELECT id FROM diary WHERE created_date=?";
        Object[] selectDiaryIdExistParams = new Object[]{created_date};
        return this.jdbcTemplate.queryForObject(selectDiaryIdExistQuery,
                int.class,
                selectDiaryIdExistParams);

    }

    public Long insertSticker(int diaryId, PostStickerReq postStickerReq ) {
        String insertStickerQuery = "INSERT INTO diary_sticker(diary_id,sticker_id,locationX,locationY,width,height, rotation, flipped) VALUES(?, ?,?, ?, ?,?,?,?) ";
        Object[] insertStickerParams=new Object[]{diaryId,postStickerReq.getStickerId(),postStickerReq.getLocationX(), postStickerReq.getLocationY(),
                postStickerReq.getWidth(), postStickerReq.getHeight(), postStickerReq.getRotation(), postStickerReq.isFlipped()};
        this.jdbcTemplate.update(insertStickerQuery, insertStickerParams);

        String lastInsertIdQuery = "SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, Long.class);
    }


    public void updateSticker(int diaryId,  PostStickerReq postStickerReq) {
        String updateStickerQuery = "update diary_sticker set sticker_id=?, locationX=?, locationY=?, width=?, height=?, rotation=?, flipped=? where diary_id = ?";
        Object[] updateStickerParams = new Object[]{postStickerReq.getStickerId(),postStickerReq.getLocationX(), postStickerReq.getLocationY(), postStickerReq.getWidth(), postStickerReq.getHeight(),
                postStickerReq.getRotation(), postStickerReq.isFlipped(), diaryId};
        this.jdbcTemplate.update(updateStickerQuery, updateStickerParams);
    }

    public List<GetStickerRes> selectStickerListByDate(int diaryId) {
        String selectStickerByDateQuery = "SELECT id, diary_id as diaryId, sticker_id as stickerId ,locationX,locationY, width, height, rotation, flipped, created_date " +
                "FROM diary_sticker " +
                "WHERE diary_id = ? ";
        Object[] selectStickerListByDateParam = new Object[]{diaryId};
        return this.jdbcTemplate.query(selectStickerByDateQuery,
                (rs,rowNum) -> new GetStickerRes(
                        rs.getLong("id"),
                        rs.getLong("diaryId"),
                        rs.getInt("stickerId"),
                        rs.getDouble("locationX"),
                        rs.getDouble("locationY"),
                        rs.getDouble("width"),
                        rs.getDouble("height"),
                        rs.getDouble("rotation"),
                        rs.getBoolean("flipped"),
                        rs.getString("created_date")
                ),selectStickerListByDateParam);
    }



    public void deleteSticker(int diaryId, int stickerId) {
        String deleteStickerQuery = "DELETE FROM diary_sticker WHERE diary_id = ? and sticker_id=?";
        Object[] deleteStickerParam = new Object[]{diaryId, stickerId};
        this.jdbcTemplate.update(deleteStickerQuery, deleteStickerParam);
    }
}
