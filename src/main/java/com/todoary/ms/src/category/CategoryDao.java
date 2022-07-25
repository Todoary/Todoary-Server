package com.todoary.ms.src.category;

import com.todoary.ms.src.category.dto.GetCategoryRes;
import com.todoary.ms.src.category.model.Category;
import com.todoary.ms.src.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CategoryDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CategoryDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Long insertCategory(Long user_id, String title, String color) {
        String insertCategoryQuery = "insert into category (user_id, title, color) values (?,?,?)";
        Object[] insertCategoryParams = new Object[]{user_id, title, color};
        this.jdbcTemplate.update(insertCategoryQuery, insertCategoryParams);

        String lastInsertIdQuery = "SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, Long.class);

    }

    public int selectExistsUsersCategoryById(long userId, long categoryId) {
        String selectExistsUsersCategoryByIdQuery = "SELECT EXISTS(SELECT user_id, id FROM category " +
                "where user_id = ? and id = ?)";
        Object[] selectExistsUsersCategoryByIdParams = new Object[]{userId, categoryId};
        return this.jdbcTemplate.queryForObject(selectExistsUsersCategoryByIdQuery, int.class, selectExistsUsersCategoryByIdParams);
    }


    public List<GetCategoryRes> selectById(Long user_id) {
        return this.jdbcTemplate.query("select id, title, color from category where user_id = ?",
                (rs, rowNum) ->
                    new GetCategoryRes(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("color")
                            )
                ,user_id
                );
    }


    public int selectExistsCategoryTitle(Long user_id, String title) {
        String selectExistsCategoryTitleQuery = "SELECT EXISTS(SELECT id FROM category " +
                "where user_id = ? and title = ?)";
        Object[] selectExistsCategoryTitleParams = new Object[]{user_id, title};
        return this.jdbcTemplate.queryForObject(selectExistsCategoryTitleQuery,int.class, selectExistsCategoryTitleParams);
    }

    public void deleteCategory(Long categoryId) {
        String deleteCategoryQuery = "DELETE FROM category WHERE id = ?";
        long deleteCategoryParam = categoryId;
        this.jdbcTemplate.update(deleteCategoryQuery, deleteCategoryParam);

    }

}
