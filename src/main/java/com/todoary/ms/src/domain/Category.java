package com.todoary.ms.src.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todos = new ArrayList<>();

    public Category(String title, Color color, Member member) {
        this.title = title;
        this.color = color;
        setMember(member);
    }

    private void setMember(Member member) {
        // 카테고리의 멤버가 바뀌는 일은 없지만...
        // 실수로 한 번 더 호출됐을 때 중복으로 더해지는 경우를 방지
        if (this.member != null) {
            this.member.getCategories().remove(this);
        }
        this.member = member;
        member.getCategories().add(this);
    }

    public void update(String title, Color color) {
        this.title = title;
        this.color = color;
    }

    public void removeAssociations(){
        this.member.getCategories().remove(this);
    }
}
