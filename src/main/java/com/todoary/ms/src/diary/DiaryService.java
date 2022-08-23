package com.todoary.ms.src.diary;


import com.todoary.ms.src.diary.dto.CreateStickerReq;
import com.todoary.ms.src.diary.dto.DeleteStickerReq;
import com.todoary.ms.src.diary.dto.ModifyStickerReq;
import com.todoary.ms.src.diary.dto.PostDiaryReq;
import com.todoary.ms.util.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.todoary.ms.util.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
@Service
public class DiaryService {

    private final DiaryDao diaryDao;
    private final DiaryProvider diaryProvider;

    @Autowired
    public DiaryService(DiaryProvider diaryProvider, DiaryDao diaryDao) {
        this.diaryProvider = diaryProvider;
        this.diaryDao = diaryDao;
    }

    public void createOrModifyDiary(Long userId, PostDiaryReq postDiaryReq, String createdDate) throws BaseException {
        try {
            diaryDao.insertOrUpdateDiary(userId, postDiaryReq, createdDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public void removeDiary(Long userId, String createdDate) throws BaseException {
        Long diaryId = diaryProvider.retrieveDiaryIdByDate(userId, createdDate);
        try {
            diaryDao.deleteDiary(diaryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<Long> createStickers(Long diaryId, List<CreateStickerReq> createdStickers) throws BaseException {
        try {
            List<Long> result = new ArrayList<>();
            for (CreateStickerReq createdSticker : createdStickers) {
                result.add(diaryDao.insertSticker(diaryId, createdSticker));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyStickers(List<ModifyStickerReq> modifiedStickers) throws BaseException {
        try {
            diaryDao.updateStickers(modifiedStickers);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void removeStickers(List<DeleteStickerReq> deletedStickers) throws BaseException {
        try {
            diaryDao.deleteStickers(deletedStickers);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
