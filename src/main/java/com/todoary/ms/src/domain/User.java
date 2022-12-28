package com.todoary.ms.src.domain;

import com.todoary.ms.src.domain.alarm.Alarm;
import com.todoary.ms.src.domain.token.Token;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
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

    @OneToOne(mappedBy = "user")
    private Token token;

    @OneToMany(mappedBy = "user")
    private List<ToDo> toDos = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Diary> diaries = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Alarm> alarms = new ArrayList<>();

    private Integer status = 1;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
