package com.todoary.ms.src.todo;

import com.todoary.ms.src.todo.dto.GetTodoByCategoryRes;
import com.todoary.ms.src.todo.dto.GetTodoByDateRes;
import com.todoary.ms.src.todo.dto.PostTodoReq;
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
    public Long insertTodo(Long userId, Long categoryId, String title, String targetDate,
                           boolean isAlarmEnabled, String targetTime) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String insertTodoQuery = "INSERT INTO todo (user_id,category_id, title, target_date, is_alarm_enabled, target_time) VALUES(?,?, ?, ?, ?, ?)";
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement pstmt = con.prepareStatement(insertTodoQuery,
                        new String[]{"id"});
                pstmt.setLong(1, userId);
                pstmt.setLong(2,categoryId);
                pstmt.setString(3, title);
                pstmt.setString(4, targetDate);
                pstmt.setBoolean(5, isAlarmEnabled);
                pstmt.setString(6, targetTime);
                return pstmt;
            }
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Transactional
    public Long insertTodo(Long userId, Long categoryId, String title, String targetDate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String insertTodoQuery = "INSERT INTO todo (user_id,category_id, title, target_date) VALUES(?, ?, ?, ?);";
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement pstmt = con.prepareStatement(insertTodoQuery,
                        new String[]{"id"});
                pstmt.setLong(1, userId);
                pstmt.setLong(2, categoryId);
                pstmt.setString(3, title);
                pstmt.setString(4, targetDate);
                return pstmt;
            }
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

//    public void insertTodoCategories(Long todoId, List<Long> categories) {
//        String insertTodoCategoryQuery = "INSERT IGNORE INTO todo_and_category (todo_id, category_id) VALUES(?, ?)";
//        this.jdbcTemplate.batchUpdate(insertTodoCategoryQuery, new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                ps.setLong(1, todoId);
//                ps.setLong(2, categories.get(i));
//            }
//
//            @Override
//            public int getBatchSize() {
//                return categories.size();
//            }
//        });
//    }

//    public void insertTodoCategory(Long todoId, Long categoryId) {
//        String insertTodoCategoryQuery = "INSERT INTO todo_and_category (todo_id, category_id) VALUES(?, ?)";
//        Object[] insertTodoCategoryParams = new Object[]{todoId, categoryId};
//        this.jdbcTemplate.update(insertTodoCategoryQuery, insertTodoCategoryParams);
//    }

    public int selectExistsUsersTodoById(Long userId, Long todoId) {
        String selectExistsUsersTodoByIdQuery = "SELECT EXISTS(SELECT user_id, id FROM todo " +
                "where user_id = ? and id = ?)";
        Object[] selectExistsUsersTodoByIdParams = new Object[]{userId, todoId};
        return this.jdbcTemplate.queryForObject(selectExistsUsersTodoByIdQuery, int.class, selectExistsUsersTodoByIdParams);
    }

    public void updateTodo(Long todoId, PostTodoReq postTodoReq) {
        String updateTodoQuery = "UPDATE todo " +
                "SET title = ?, category_id = ?, target_date = ?,  is_alarm_enabled = ?, target_time = ? " +
                "WHERE id = ?";
        Object[] updateTodoParams = new Object[]{postTodoReq.getTitle(),postTodoReq.getCategoryId(), postTodoReq.getTargetDate(),
                postTodoReq.isAlarmEnabled(), postTodoReq.getTargetTime(), todoId};
        this.jdbcTemplate.update(updateTodoQuery, updateTodoParams);
    }

//    @Transactional
//    public void deleteAndUpdateTodoCategories(Long todoId, List<Long> categories) {
//        String inParameter = String.join(",", Collections.nCopies(categories.size(), "?"));
//        String updateTodoCategoriesQuery = String.format("DELETE FROM todo_and_category WHERE todo_id = ? and category_id NOT IN(%s)", inParameter);
//        categories.add(0, todoId);
//        Object[] updateTodoCategoriesParams = categories.toArray();
//        this.jdbcTemplate.update(updateTodoCategoriesQuery, updateTodoCategoriesParams);
//        insertTodoCategories(todoId, categories);
//    }

    public void deleteTodo(Long todoId) {
        String deleteTodoQuery = "DELETE FROM todo WHERE id = ?";
        Long deleteTodoParam = todoId;
        this.jdbcTemplate.update(deleteTodoQuery, deleteTodoParam);
    }

    public List<GetTodoByDateRes> selectTodoListByDate(Long userId, String targetDate) {
        String selectTodosByDateQuery = "SELECT todo.id, todo.is_pinned, todo.is_checked, todo.title, target_date, todo.is_alarm_enabled, TIME_FORMAT(todo.target_time, '%H:%i') as target_time, todo.created_at, todo.category_id,  category.title, category.color " +
                "from todo INNER JOIN category ON todo.category_id = category.id WHERE todo.user_id = ? and todo.target_date = ? " +
                "ORDER BY todo.target_date, todo.target_time, todo.created_at";
        Object[] selectTodosByDateParams = new Object[]{userId, targetDate};

        return this.jdbcTemplate.query(selectTodosByDateQuery,
                (rs, rowNum) -> new GetTodoByDateRes(
                        rs.getLong("id"),
                        rs.getBoolean("is_pinned"),
                        rs.getBoolean("is_checked"),
                        rs.getString("todo.title"),
                        rs.getString("target_date"),
                        rs.getBoolean("is_alarm_enabled"),
                        rs.getString("target_time"),
                        rs.getString("created_at"),
                        rs.getLong("category_id"),
                        rs.getString("category.title"),
                        rs.getInt("color")
                        )
                ,selectTodosByDateParams);
    }

    public List<GetTodoByCategoryRes> selectTodoListByCategory(Long userId, Long categoryId) {
        String selectTodosByCategoryQuery = "SELECT todo.id, todo.is_checked, todo.title, todo.target_date, todo.is_alarm_enabled, TIME_FORMAT(todo.target_time, '%H:%i') as target_time, todo.created_at, todo.category_id, category.title, category.color " +
                "FROM todo INNER JOIN category ON todo.category_id = category.id WHERE category_id = ? " +
                "ORDER BY todo.target_date, todo.target_time, todo.created_at";
        Long selectTodosByCategoryParam = categoryId;
        return this.jdbcTemplate.query(selectTodosByCategoryQuery,
                (rs, rowNum) -> new GetTodoByCategoryRes(
                        rs.getLong("id"),
                        rs.getBoolean("is_checked"),
                        rs.getString("todo.title"),
                        rs.getString("target_date"),
                        rs.getBoolean("is_alarm_enabled"),
                        rs.getString("target_time"),
                        rs.getString("created_at"),
                        rs.getLong("category_id"),
                        rs.getString("category.title"),
                        rs.getInt("color")
                        ), selectTodosByCategoryParam);
    }

    public void updateTodoCheck(Long todoId, boolean isChecked) {
        String updateTodoStatusQuery = "UPDATE todo SET is_checked = ? WHERE id = ?";
        Object[] updateTodoStatusParams = new Object[]{isChecked, todoId};
        this.jdbcTemplate.update(updateTodoStatusQuery, updateTodoStatusParams);
    }

    public void updateTodoPin(Long todoId, boolean isPinned) {
        String updateTodoStatusQuery = "UPDATE todo SET is_pinned = ? WHERE id = ?";
        Object[] updateTodoStatusParams = new Object[]{isPinned, todoId};
        this.jdbcTemplate.update(updateTodoStatusQuery, updateTodoStatusParams);
    }

    public List<Integer> selectDaysHavingTodoInMonth(Long userId, String yearAndMonth) {
        String selectDaysHavingTodoInMonthQuery = "SELECT DAY(target_date) as day " +
                "FROM todo WHERE user_id=? and ? = DATE_FORMAT(target_date, '%Y-%m') " +
                "GROUP by day ORDER BY day";
        Object[] selectDaysHavingTodoInMonthParams = new Object[]{userId, yearAndMonth};
        return this.jdbcTemplate.query(selectDaysHavingTodoInMonthQuery,
                (rs, rowNum) -> (rs.getInt("day")), selectDaysHavingTodoInMonthParams);
    }
}
