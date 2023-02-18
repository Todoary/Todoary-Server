package com.todoary.ms.src.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Diary extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;

    private String content;

    private LocalDate createdDate;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sticker> stickers = new ArrayList<>();

    /*---Constructor---*/
    public Diary(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        setMember(member);
    }



    /*---Setter---*/
    private void setMember(Member member) {
        if (this.member != null) {
            this.member.removeDiary(this);
        }
        this.member = member;
        member.addDiary(this);
    }

    /*---Method---*/
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void updateDate(LocalDate createdDate) {
        this.createdDate=createdDate;
    }

    public void removeAssociations() {
        this.member.removeDiary(this);
    }

    public boolean has(Member member) {
        return this.member == member;
    }



}
