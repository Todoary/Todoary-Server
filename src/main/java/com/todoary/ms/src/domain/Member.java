package com.todoary.ms.src.domain;

import com.todoary.ms.src.domain.token.FcmToken;
import com.todoary.ms.src.domain.token.RefreshToken;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;

    private String nickname;

    private String email;

    private String password;

    @Column(name = "profile_img_url")
    private String profileImgUrl;

    private String introduce;

    private String role;

    @Embedded
    private ProviderAccount providerAccount;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private FcmToken fcmToken;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todos = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Diary> diaries = new ArrayList<>();

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private RemindAlarm remindAlarm;

    private Boolean isTermsEnable;

    private Boolean toDoAlarmEnable = true;

    private Boolean remindAlarmEnable = true;

    private Boolean dailyAlarmEnable = true;

    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime modifiedAt;

    /*---Constructor---*/

    @Builder
    public Member(String name, String nickname, String email, String password, ProviderAccount providerAccount, Boolean isTermsEnable) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.providerAccount = providerAccount;
        this.isTermsEnable = isTermsEnable;
    }

    /*---Setter---*/
    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setFcmToken(FcmToken fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void setRemindAlarm(RemindAlarm remindAlarm) {
        this.remindAlarm = remindAlarm;
    }

    public void addTodo(Todo todo) {
        this.todos.add(todo);
    }

    public void addDiary(Diary diary) {
        this.diaries.add(diary);
    }

    public void changeRemindAlarm(RemindAlarm remindAlarm) {
        this.remindAlarm = remindAlarm;
    }

    public void addCategory(Category category) {
        this.categories.add(category);
    }

    public void removeTodo(Todo todo) {
        this.todos.remove(todo);
    }

    public void removeRefreshToken() {
        this.refreshToken = null;
    }

    public void removeFcmToken() {
        this.fcmToken = null;
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
    }

    /*---Method---*/
<<<<<<< HEAD
=======
    public static Member create(String name, String nickname, String email, String password, Integer isTermsEnable) {
        return new Member(name, nickname,email, password, isTermsEnable);
    }

    public boolean hasCategoryNamed(String title) {
        return getCategories().stream()
                .anyMatch(category -> category.getTitle().equals(title));
    }

    public static Member createByOauth(String name, String email, ProviderAccount providerAccount, Integer isTermsEnable) {
        return new Member(name, email, providerAccount, isTermsEnable);
    }
>>>>>>> Develop
}
