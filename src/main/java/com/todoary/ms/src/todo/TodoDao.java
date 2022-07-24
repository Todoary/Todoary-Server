package com.todoary.ms.src.todo;

import com.todoary.ms.src.category.model.Category;
import com.todoary.ms.src.todo.dto.GetTodoByCategoryRes;
import com.todoary.ms.src.todo.dto.GetTodoByDateRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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

    public void insertTodoCategories(long todoId, List<Long> categories) {
        String insertTodoCategoryQuery = "INSERT INTO todo_and_category (todo_id, category_id) VALUES(?, ?)";
        this.jdbcTemplate.batchUpdate(insertTodoCategoryQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, todoId);
                ps.setLong(2, categories.get(i));
            }

            @Override
            public int getBatchSize() {
                return categories.size();
            }
        });
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

    public List<GetTodoByDateRes> selectTodoListByDate(long userId, String targetDate) {
        String selectTodosByDateQuery = "SELECT id, is_checked, title, is_alarm_enabled, TIME_FORMAT(target_time, '%H:%i') as target_time " +
                "from todo WHERE user_id = ? and target_date = ? " +
                "ORDER BY target_date, target_time";
        Object[] selectTodosByDateParams = new Object[]{userId, targetDate};
        String selectCategoriesByTodoIdQuery = "SELECT category_id, c.title, c.color\n" +
                "FROM todo_and_category as tc JOIN category c on tc.category_id = c.id\n" +
                "WHERE todo_id = ? " +
                "ORDER BY category_id";
        return this.jdbcTemplate.query(selectTodosByDateQuery,
                (rs, rowNum) -> new GetTodoByDateRes(
                        rs.getLong("id"),
                        rs.getBoolean("is_checked"),
                        rs.getString("title"),
                        rs.getBoolean("is_alarm_enabled"),
                        rs.getString("target_time"),
                        this.jdbcTemplate.query(selectCategoriesByTodoIdQuery,
                                (rs2, rowNum2) -> new Category(
                                        rs2.getLong("category_id"),
                                        rs2.getString("title"),
                                        rs2.getString("color")
                                ), rs.getLong("id"))
                ), selectTodosByDateParams);
    }

    public List<GetTodoByCategoryRes> selectTodoListByCategory(long userId, long categoryId) {
        String selectTodosByCategoryQuery = "SELECT todo_id, is_checked, title, target_date, is_alarm_enabled, TIME_FORMAT(target_time, '%H:%i') as target_time " +
                "FROM todo_and_category JOIN todo t on t.id = todo_and_category.todo_id " +
                "WHERE category_id = ? " +
                "ORDER BY target_date, target_time";
        long selectTodosByCategoryParam = categoryId;
        String selectCategoriesByTodoIdQuery = "SELECT category_id, c.title, c.color " +
                "FROM todo_and_category as tc JOIN category c on tc.category_id = c.id " +
                "WHERE todo_id = ? " +
                "ORDER BY category_id";
        return this.jdbcTemplate.query(selectTodosByCategoryQuery,
                (rs, rowNum) -> new GetTodoByCategoryRes(
                        rs.getLong("todo_id"),
                        rs.getBoolean("is_checked"),
                        rs.getString("title"),
                        rs.getString("target_date"),
                        rs.getBoolean("is_alarm_enabled"),
                        rs.getString("target_time"),
                        this.jdbcTemplate.query(selectCategoriesByTodoIdQuery,
                                (rs2, rowNum2) -> new Category(
                                        rs2.getLong("category_id"),
                                        rs2.getString("title"),
                                        rs2.getString("color")
                                ), rs.getLong("todo_id"))
                ), selectTodosByCategoryParam);
    }
}
