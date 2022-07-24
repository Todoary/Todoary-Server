package com.todoary.ms.src.todo;

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
import java.util.Objects;

@Repository
public class TodoDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TodoDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public long insertTodo(long userId, String title, String targetDate,
                           boolean isAlarmEnabled, String targetTime) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String insertTodoQuery = "INSERT INTO todo (user_id, title, target_date, is_alarm_enabled, target_time) VALUES(?, ?, ?, ?, ?)";
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement pstmt = con.prepareStatement(insertTodoQuery,
                        new String[]{"id"});
                pstmt.setLong(1, userId);
                pstmt.setString(2, title);
                pstmt.setString(3, targetDate);
                pstmt.setBoolean(4, isAlarmEnabled);
                pstmt.setString(5, targetTime);
                return pstmt;
            }
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Transactional
    public long insertTodo(long userId, String title, String targetDate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String insertTodoQuery = "INSERT INTO todo (user_id, title, target_date) VALUES(?, ?, ?);";
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement pstmt = con.prepareStatement(insertTodoQuery,
                        new String[]{"id"});
                pstmt.setLong(1, userId);
                pstmt.setString(2, title);
                pstmt.setString(3, targetDate);
                return pstmt;
            }
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public void insertTodoCategory(long todoId, long categoryId) {
        String insertTodoCategoryQuery = "INSERT INTO todo_and_category (todo_id, category_id) VALUES(?, ?)";
        Object[] insertTodoCategoryParams = new Object[]{todoId, categoryId};
        this.jdbcTemplate.update(insertTodoCategoryQuery, insertTodoCategoryParams);
    }

    public int selectExistsUsersTodoById(long userId, long todoId) {
        String selectExistsUsersTodoByIdQuery = "SELECT EXISTS(SELECT user_id, id FROM todo " +
                "where user_id = ? and id = ?)";
        Object[] selectExistsUsersTodoByIdParams = new Object[]{userId, todoId};
        return this.jdbcTemplate.queryForObject(selectExistsUsersTodoByIdQuery, int.class, selectExistsUsersTodoByIdParams);
    }

    public void deleteTodo(long todoId) {
        String deleteTodoQuery = "DELETE FROM todo WHERE id = ?";
        long deleteTodoParam = todoId;
        this.jdbcTemplate.update(deleteTodoQuery, deleteTodoParam);
    }
}
