package com.todoary.ms.src.domain;

import com.todoary.ms.src.domain.alarm.ToDoAlarm;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "todo")
public class ToDo {
    @Id
    @GeneratedValue
    @Column(name = "todo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate targetDate;

    private LocalDateTime targetTime;

    @OneToOne(mappedBy = "todo", fetch = FetchType.LAZY)
    private ToDoAlarm alarm;

    private Boolean isAlarmEnabled;

    private Boolean isChecked;

    private Boolean isPinned;

    private String password;

    private Integer status = 1;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
