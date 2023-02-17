package com.todoary.ms.src.domain;

import com.todoary.ms.src.domain.token.FcmToken;
import com.todoary.ms.src.domain.token.RefreshToken;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;

    private String nickname;

    private String email;

    private String password;

    @Column(name = "profile_img_url")
    private String profileImgUrl = "https://todoarybucket.s3.ap-northeast-2.amazonaws.com/todoary/users/admin/default_profile_img.jpg";

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

    /*---Constructor---*/

    @Builder
    public Member(String name, String nickname, String email, String password, String role, ProviderAccount providerAccount, Boolean isTermsEnable) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
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

    public void changeProfileImg(String newProfileImgUrl) {
        this.profileImgUrl = newProfileImgUrl;
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

    public void activeTodoAlarm(boolean toDoAlarmEnable){
        this.toDoAlarmEnable = toDoAlarmEnable;
    }

    public void activeDailyAlarm(boolean dailyAlarmEnable ){
        this.dailyAlarmEnable  = dailyAlarmEnable;
    }

    public void activeRemindAlarm(boolean remindAlarmEnable ){
        this.remindAlarmEnable  = remindAlarmEnable;
    }

    public void activeTermsStatus(boolean isTermsEnable ){
        this.isTermsEnable  = isTermsEnable;
    }

    /*---Method---*/
    public boolean hasCategoryNamed(String title) {
        return getCategories().stream()
                .anyMatch(category -> category.getTitle().equals(title));
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public boolean hasRefreshTokenCode(String refreshTokenCode) {
        return this.getRefreshToken().hasCode(refreshTokenCode);
    }

    public void update(String nickname, String introduce) {
        this.nickname = nickname;
        this.introduce = introduce;
    }
}
