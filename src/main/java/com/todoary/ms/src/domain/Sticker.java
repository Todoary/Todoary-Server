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

    @Embedded
    private StickerShape shape;

    /*---Constructor---*/
    @Builder
    public Sticker(Diary diary, StickerType type, StickerShape shape) {
        mapDiary(diary);
        this.type = type;
        this.shape = shape;
    }

    /*---Setter---*/

    private void mapDiary(Diary diary) {
        if (this.diary != null) {
            this.diary.getStickers().remove(this);
        }
        this.diary = diary;
        diary.getStickers().add(this);
    }

    public void update(StickerType type, StickerShape shape) {
        this.type = type;
        this.shape = shape;
    }

    public void removeAssociation() {
        this.diary.removeSticker(this);
    }
}
