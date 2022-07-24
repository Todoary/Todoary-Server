package com.todoary.ms.src.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.transaction.Transactional;

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
        String insertTodoQuery = "INSERT INTO todo (user_id, title, target_date, is_alarm_enabled, target_time) VALUES(?, ?, ?, ?, ?)";
        Object[] insertTodoParams = new Object[]{userId, title, targetDate, isAlarmEnabled, targetTime};
        this.jdbcTemplate.update(insertTodoQuery, insertTodoParams);

        String lastInsertIdQuery = "SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, long.class);
    }

    @Transactional
    public long insertTodo(long userId, String title, String targetDate) {
        String insertTodoQuery = "INSERT INTO todo (user_id, title, target_date) VALUES(?, ?, ?);";
        Object[] insertTodoParams = new Object[]{userId, title, targetDate};
        this.jdbcTemplate.update(insertTodoQuery, insertTodoParams);

        String lastInsertIdQuery = "SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, long.class);
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
