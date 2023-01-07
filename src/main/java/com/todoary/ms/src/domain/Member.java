package com.todoary.ms.src.domain;

import com.todoary.ms.src.domain.alarm.RemindAlarm;
import com.todoary.ms.src.domain.token.FcmToken;
import com.todoary.ms.src.domain.token.RefreshToken;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter @NoArgsConstructor
@Entity
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    private String nickname;

    private String email;

    private String password;

    @Column(name = "profile_img_rul")
    private String profileImgUrl;

    private String introduce;

    private String role;

    // provider는 추후 Provider객체로 병합
    private String provider;

    private String providerId;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private RefreshToken refreshToken;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private FcmToken fcmToken;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todos = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Diary> diaries = new ArrayList<>();

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private RemindAlarm remindAlarm;

    // @OneToMany(mappedBy = "member")
    // private List<ToDoAlarm> toDoAlarms = new ArrayList<>();

    private Integer status = 1;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /*---Constructor---*/
    public Member(String name, String nickname, String email, String password) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }
    /*---Getter---*/
    public Long getId() {
        return this.id;
    }

    /*---Setter---*/
    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setFcmToken(FcmToken fcmToken) {
        this.fcmToken = fcmToken;
    }

    /*---Method---*/
    public static Member create(String name, String nickname, String email, String password) {
        return new Member(name, nickname,email, password);
    }

    public Optional<Category> findCategoryNamed(String title) {
        return getCategories().stream()
                .filter(category -> category.getTitle().equals(title)).findAny();
    }
}
