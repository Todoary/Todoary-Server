package com.todoary.ms.src.domain;

import javax.persistence.*;

@Entity
public class Sticker extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sticker_id")
    private Long id;

    private String name;

    private String stickerImgUrl;
}
