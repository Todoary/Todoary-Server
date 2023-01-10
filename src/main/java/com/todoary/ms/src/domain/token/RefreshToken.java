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

    private String token;

    /*---Constructor---*/
    public RefreshToken(Member member, String token) {
        this.member = member;
        member.setRefreshToken(this);
        this.token = token;
    }

    /*---Setter---*/
    public void changeToken(String token) {
        this.token = token;
    }

    /*---Method---*/
    public void removeAssociations() {
        this.member.removeRefreshToken();
    }

    public Boolean belongs(Member member) {
        if (this.member == member) {
            return true;
        }

        return false;
    }
}
