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
