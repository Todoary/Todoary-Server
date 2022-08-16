package com.todoary.ms.src.sticker;


import com.todoary.ms.src.sticker.dto.GetStickerRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;


@Repository
public class StickerDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StickerDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    public List<GetStickerRes> selectStickerListByDate(Long diaryId) {
        String selectStickerByDateQuery = "SELECT id, user_id, diary_id,sticker_id,x,y, created_date " +
                "FROM diary_sticker " +
                "WHERE diary_id = ? and DATE(?)=DATE(created_date) " +
                "ORDER BY created_date ";
        Long selectStickerByDateParams=diaryId;
        return this.jdbcTemplate.query(selectStickerByDateQuery,
                (rs,rowNum) -> new GetStickerRes(
                        rs.getLong("id"),
                        rs.getLong("userId"),
                        rs.getLong("diaryId"),
                        rs.getInt("stickerId"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getString("created_date")
                ),selectStickerByDateParams);

    }


}
