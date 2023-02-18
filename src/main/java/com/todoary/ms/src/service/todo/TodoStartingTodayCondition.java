package com.todoary.ms.src.service.todo;

import com.querydsl.core.types.Predicate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static com.todoary.ms.src.domain.QTodo.todo;


@Component
public class TodoStartingTodayCondition implements TodoByCategoryCondition {
    @Override
    public Predicate getPredicate() {
        return todo.targetDate.goe(LocalDate.now());
    }
}
