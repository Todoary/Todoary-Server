package com.todoary.ms.src.category;

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

    public int selectExistsUsersTodoById(long userId, long todoId) {
        String selectExistsUsersTodoByIdQuery = "SELECT EXISTS(SELECT user_id, id FROM todo " +
                "where user_id = ? and id = ?)";
        Object[] selectExistsUsersTodoByIdParams = new Object[]{userId, todoId};
        return this.jdbcTemplate.queryForObject(selectExistsUsersTodoByIdQuery, int.class, selectExistsUsersTodoByIdParams);
    }
}
