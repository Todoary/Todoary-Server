package com.todoary.ms.src.domain.token;

import com.todoary.ms.src.domain.Member;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue
    @Column(name = "token_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String value;

    private Integer status = 1;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /*---Constructor---*/
    private RefreshToken(Member member, String value) {
        this.member = member;
        this.value = value;
    }

    /*---Getter---*/

    /*---Method---*/
    public static RefreshToken create(Member member, String value) {
        return new RefreshToken(member, value);
    }

    public void register(Member member) {
        this.member = member;
        member.setRefreshToken(this);
    }
}
