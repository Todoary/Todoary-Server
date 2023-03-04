package com.todoary.ms.src.domain;

import com.todoary.ms.src.domain.token.FcmToken;
import com.todoary.ms.src.domain.token.RefreshToken;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
@EqualsAndHashCode(of = {"email", "providerAccount"}, callSuper = false)
@Entity
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;

    private String nickname;

    @NotNull
    private String email;

    private String password;

    @Column(name = "profile_img_url")
    private String profileImgUrl;

    private String introduce;

    @Builder.Default
    private String role = "USER_ROLE";

    @Embedded
    @NotNull
    @Builder.Default
    private ProviderAccount providerAccount = ProviderAccount.none();

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private FcmToken fcmToken;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Todo> todos = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Diary> diaries = new ArrayList<>();

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private RemindAlarm remindAlarm;

    @Builder.Default
    private Boolean isTermsEnable = true;

    @Builder.Default
    private Boolean toDoAlarmEnable = true;

    @Builder.Default
    private Boolean remindAlarmEnable = true;

    @Builder.Default
    private Boolean dailyAlarmEnable = true;

    @Builder.Default
    private Integer status = 1;

    /*---Constructor---*/

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

    public void activeTodoAlarm(boolean toDoAlarmEnable) {
        this.toDoAlarmEnable = toDoAlarmEnable;
    }

    public void activeDailyAlarm(boolean dailyAlarmEnable) {
        this.dailyAlarmEnable = dailyAlarmEnable;
    }

    public void activeRemindAlarm(boolean remindAlarmEnable) {
        this.remindAlarmEnable = remindAlarmEnable;
    }

    public void activeTermsStatus(boolean isTermsEnable) {
        this.isTermsEnable = isTermsEnable;
    }

    public void removeDiary(Diary diary) {
        this.diaries.remove(diary);
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

    public boolean isDeactivated() {
        return status == 0;
    }

    public void activate() {
        this.status = 1;
    }

    public void deactivate() {
        this.status = 0;
        removeRefreshToken();
        removeFcmToken();
    }

    public boolean hasRefreshToken() {
        return this.refreshToken != null;
    }

    public RefreshToken updateRefreshToken(String code) {
        this.refreshToken.changeCode(code);
        return this.refreshToken;
    }

    public FcmToken updateFcmToken(String code) {
        this.fcmToken.changeCode(code);
        return this.fcmToken;
    }
}
