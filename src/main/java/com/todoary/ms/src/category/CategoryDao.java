package com.todoary.ms.src.category;

import com.todoary.ms.src.category.model.Category;
import com.todoary.ms.src.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class CategoryDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CategoryDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insertCategory(Long user_id, String title, String color) {
        String insertCategoryQuery = "insert into category (user_id, title, color) values (?,?,?)";
        Object[] insertCategoryParams = new Object[]{user_id, title, color};

        this.jdbcTemplate.update(insertCategoryQuery, insertCategoryParams);

    }

    public int selectExistsUsersCategoryById(long userId, long categoryId) {
        String selectExistsUsersCategoryByIdQuery = "SELECT EXISTS(SELECT user_id, id FROM category " +
                "where user_id = ? and id = ?)";
        Object[] selectExistsUsersCategoryByIdParams = new Object[]{userId, categoryId};
        return this.jdbcTemplate.queryForObject(selectExistsUsersCategoryByIdQuery, int.class, selectExistsUsersCategoryByIdParams);
    }

    public Category selectById(Long user_id) {
        String selectByIdQuery = "select id, category_img_id, title, color from category where id = ?";
        return this.jdbcTemplate.queryForObject(selectByIdQuery,
                (rs, rowNum) -> new User(
                        rs.getLong("id"),
                        rs.getString("name"),
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

}
