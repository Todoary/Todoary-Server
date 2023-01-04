package com.todoary.ms.src.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
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

    private LocalDate targetDate;
    private LocalTime targetTime;

    private Boolean isAlarmEnabled;

    private Boolean isChecked;

    private Boolean isPinned;

    @Builder
    public Todo(Member member, Category category, String title, LocalDate targetDate, LocalTime targetTime, Boolean isAlarmEnabled) {
        setMember(member);
        setCategory(category);
        this.title = title;
        setTarget(targetDate, targetTime);
        enableAlarm(isAlarmEnabled);
    }

    public void update(String title, Category category, LocalDate targetDate, LocalTime targetTime, Boolean isAlarmEnabled) {
        this.title = title;
        setCategory(category);
        setTarget(targetDate, targetTime);
        enableAlarm(isAlarmEnabled);
    }

    public void setTarget(LocalDate targetDate, LocalTime targetTime) {
        this.targetDate = targetDate;
        this.targetTime = targetTime;
    }

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

    public void check(boolean isChecked){
        this.isChecked = isChecked;
    }

    public void pin(boolean isPinned){
        this.isPinned = isPinned;
    }

    public void enableAlarm(boolean isAlarmEnabled){
        this.isAlarmEnabled = isAlarmEnabled;
    }

    public void removeAssociations() {
        this.member.getTodos().remove(this);
        this.category.getTodos().remove(this);
    }
}
