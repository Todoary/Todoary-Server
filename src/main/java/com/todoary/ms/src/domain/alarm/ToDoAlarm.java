package com.todoary.ms.src.domain.alarm;

import com.todoary.ms.src.domain.ToDo;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@DiscriminatorValue("todo")
public class ToDoAlarm extends Alarm {
    @OneToOne
    @JoinColumn(name = "todo_id")
    private ToDo todo;
}
