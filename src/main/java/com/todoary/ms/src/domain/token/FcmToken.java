package com.todoary.ms.src.domain.token;

import com.todoary.ms.src.domain.BaseTimeEntity;
import com.todoary.ms.src.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class FcmToken extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_token_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String code;

    /*---Constructor---*/
    public FcmToken(Member member, String code) {
        this.member = member;
        member.setFcmToken(this);
        this.code = code;
    }

    /*---Setter---*/
    public void changeCode(String code) {
        this.code = code;
    }

    /*---Method---*/
}
