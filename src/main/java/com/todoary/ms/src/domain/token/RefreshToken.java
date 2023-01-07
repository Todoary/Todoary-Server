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
public class RefreshToken extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String jwt;

    /*---Constructor---*/
    public RefreshToken(Member member, String jwt) {
        this.member = member;
        member.setRefreshToken(this);
        this.jwt = jwt;
    }

    /*---Setter---*/
    public void changeJwt(String jwt) {
        this.jwt = jwt;
    }

    /*---Method---*/
    public void removeAssociations() {
        this.member.removeRefreshToken();
    }
}
