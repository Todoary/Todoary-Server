package com.todoary.ms.src.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
public class Todo extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String title;

    @Column(nullable = false)
    private Boolean isAlarmEnabled = false;

    private LocalDate targetDate;

    private LocalTime targetTime;

    @Column(nullable = false)
    private Boolean isChecked = false;

    @Column(nullable = false)
    private Boolean isPinned = false;

    /*---Constructor---*/
    @Builder
    public Todo(Member member, Category category, String title, Boolean isAlarmEnabled, LocalDate targetDate, LocalTime targetTime) {
        setMember(member);
        setCategory(category);
        this.title = title;
        updateAlarm(isAlarmEnabled, targetDate, targetTime);
    }

    /*---Setter---*/
    private void setMember(Member member) {
        if (this.member != null){
            this.member.getTodos().remove(this);
        }
        this.member = member;
        member.getTodos().add(this);
    }

    private void setCategory(Category category) {
        if (this.category != null){
            this.category.getTodos().remove(this);
        }
        this.category = category;
        category.getTodos().add(this);
    }

    public void removeAssociations() {
        this.member.removeTodo(this);
        this.category.removeTodo(this);
    }
    /*---Method---*/
    public void update(String title, Category category, Boolean isAlarmEnabled, LocalDate targetDate, LocalTime targetTime) {
        this.title = title;
        setCategory(category);
        updateAlarm(isAlarmEnabled, targetDate, targetTime);
    }

    public void updateAlarm(boolean isAlarmEnabled, LocalDate targetDate, LocalTime targetTime) {
        this.isAlarmEnabled = isAlarmEnabled;
        this.targetDate = targetDate;
        this.targetTime = targetTime;
    }

    public void check(boolean isChecked){
        this.isChecked = isChecked;
    }

    public void pin(boolean isPinned){
        this.isPinned = isPinned;
    }
}
