package com.todoary.ms.src.service.todo;

import com.querydsl.core.types.Predicate;

public interface TodoByCategoryCondition {
    public Predicate getPredicate();
}
