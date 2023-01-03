package com.todoary.ms.src.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Category extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(length = 40)
    private String title;

    @Embedded
    private Color color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Category(String title, Color color, Member member) {
        this.title = title;
        this.color = color;
        setMember(member);
    }

    private void setMember(Member member) {
        this.member = member;
        member.getCategories().add(this);
    }

    public void update(String title, Color color) {
        this.title = title;
        this.color = color;
    }
}
