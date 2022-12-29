package com.todoary.ms.src.domain;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Token {
    @Id
    @GeneratedValue
    @Column(name = "token_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String refreshToken;
    
    private String fcmToken;

    private Integer status = 1;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
