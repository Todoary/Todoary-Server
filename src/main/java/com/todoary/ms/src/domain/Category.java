package com.todoary.ms.src.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.todoary.ms.src.domain.Category.InitialCategoryValue.initialColor;
import static com.todoary.ms.src.domain.Category.InitialCategoryValue.initialTitle;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
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

    /*---Constructor---*/
    public Category(String title, Color color, Member member) {
        this.title = title;
        this.color = color;
        if (member != null) {
            setMember(member);
        }
    }

    // 멤버 새로 생성될때마다 기본으로 갖고 있어야 하는 초기 카테고리
    public static Category createInitialCategoryOf(Member newMember) {
        return new Category(initialTitle, initialColor, newMember);
    }

    /*---Setter---*/
    private void setMember(Member member) {
        // 카테고리의 멤버가 바뀌는 일은 없지만...
        // 실수로 한 번 더 호출됐을 때 중복으로 더해지는 경우를 방지
        if (this.member != null) {
            this.member.removeCategory(this);
        }
        this.member = member;
        member.addCategory(this);
    }

    /*---Method---*/
    public void update(String title, Color color) {
        if (!this.title.equals(title))
            this.title = title;
        update(color);
    }

    public void update(Color color) {
        if (!this.color.equals(color))
            this.color = color;
    }

    public void removeAssociations() {
        this.member.removeCategory(this);
    }

    public void removeTodo(Todo todo) {
        this.todos.remove(todo);
    }

    public boolean has(Member member) {
        return this.member == member;
    }

    public void addTodo(Todo todo) {
        this.todos.add(todo);
    }

    public static class InitialCategoryValue {
        public static final String initialTitle = "일상☘️";
        public static final Color initialColor = Color.from(1);
    }

}
