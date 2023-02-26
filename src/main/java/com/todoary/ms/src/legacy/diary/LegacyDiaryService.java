package com.todoary.ms.src.legacy.diary;


import com.todoary.ms.src.legacy.diary.dto.CreateStickerReq;
import com.todoary.ms.src.legacy.diary.dto.DeleteStickerReq;
import com.todoary.ms.src.legacy.diary.dto.ModifyStickerReq;
import com.todoary.ms.src.legacy.diary.dto.PostDiaryReq;
import com.todoary.ms.src.legacy.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.todoary.ms.src.common.response.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
@Service
public class LegacyDiaryService {

    private final LegacyDiaryDao legacyDiaryDao;
    private final LegacyDiaryProvider legacyDiaryProvider;

    @Autowired
    public LegacyDiaryService(LegacyDiaryProvider legacyDiaryProvider, LegacyDiaryDao legacyDiaryDao) {
        this.legacyDiaryProvider = legacyDiaryProvider;
        this.legacyDiaryDao = legacyDiaryDao;
    }

    public void createOrModifyDiary(Long userId, PostDiaryReq postDiaryReq, String createdDate) throws BaseException {
        try {
            legacyDiaryDao.insertOrUpdateDiary(userId, postDiaryReq, createdDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public void removeDiary(Long userId, String createdDate) throws BaseException {
        Long diaryId = legacyDiaryProvider.retrieveDiaryIdByDate(userId, createdDate);
        try {
            legacyDiaryDao.deleteDiary(diaryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<Long> createStickers(Long diaryId, List<CreateStickerReq> createdStickers) throws BaseException {
        try {
            List<Long> result = new ArrayList<>();
            for (CreateStickerReq createdSticker : createdStickers) {
                result.add(legacyDiaryDao.insertSticker(diaryId, createdSticker));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyStickers(List<ModifyStickerReq> modifiedStickers) throws BaseException {
        try {
            legacyDiaryDao.updateStickers(modifiedStickers);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void removeStickers(List<DeleteStickerReq> deletedStickers) throws BaseException {
        try {
            legacyDiaryDao.deleteStickers(deletedStickers);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
