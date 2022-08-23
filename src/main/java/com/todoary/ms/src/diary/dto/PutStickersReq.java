package com.todoary.ms.src.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PutStickersReq {
    List<CreateStickerReq> created;
    List<ModifyStickerReq> modified;
    List<DeleteStickerReq> deleted;
}
