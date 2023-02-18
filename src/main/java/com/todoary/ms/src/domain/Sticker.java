package com.todoary.ms.src.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Sticker extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sticker_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @Embedded
    private StickerType type;

    private Double locationX;

    private Double locationY;

    private Double width;

    private Double height;

    private Double rotation;

    @Column(nullable = false)
    private Boolean flipped = false;

    /*---Constructor---*/
    @Builder
    public Sticker(Diary diary, StickerType type, Double locationX, Double locationY, Double width, Double height, Double rotation, Boolean flipped) {
        setDiary(diary);
        this.type = type;
        updatePosition(locationX,locationY,width,height,rotation,flipped);
    }

    /*---Setter---*/

    private void setDiary(Diary diary) {
        if (this.diary != null){
            this.diary.getStickers().remove(this);
        }
        this.diary = diary;
        diary.getStickers().add(this);
    }

    /*---Method---*/
    public void updatePosition(Double locationX, Double locationY, Double width, Double height, Double rotation, Boolean flipped) {
        this.locationX = locationX;
        this.locationY = locationY;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.flipped = flipped;
    }
}
