package com.todoary.ms.src.category;

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

    public void insertCategory(Long user_id,String title, String color) {
        String insertCategoryQuery = "insert into category (user_id, title, color) values (?,?,?)";
        Object[] insertCategoryParams = new Object[]{user_id, title, color};

        this.jdbcTemplate.update(insertCategoryQuery, insertCategoryParams);

    }
}
