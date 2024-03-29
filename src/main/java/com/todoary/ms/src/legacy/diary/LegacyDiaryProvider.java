package com.todoary.ms.src.legacy.diary;

import com.todoary.ms.src.legacy.diary.dto.GetDiaryByDateRes;
import com.todoary.ms.src.legacy.diary.dto.GetStickerRes;
import com.todoary.ms.src.legacy.BaseException;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LegacyDiaryProvider {

    private final LegacyDiaryDao legacyDiaryDao;


    @Autowired
    public LegacyDiaryProvider(LegacyDiaryDao legacyDiaryDao) {
        this.legacyDiaryDao = legacyDiaryDao;
    }

    public boolean checkUsersDiaryById(Long userId, String createdDate) throws BaseException {
        try {
            return (legacyDiaryDao.selectExistsUsersDiaryByDate(userId, createdDate) == 1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void assertUsersDiaryValidByDate(Long userId, String createdDate) throws BaseException {
        if (!checkUsersDiaryById(userId, createdDate))
            throw new BaseException(BaseResponseStatus.USERS_DIARY_NOT_EXISTS);
    }

    public GetDiaryByDateRes retrieveDiaryByDate(Long diaryId) throws BaseException {
        try {
            return legacyDiaryDao.selectDiaryByDate(diaryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<Integer> retrieveIsDiaryInMonth(Long userId, String yearAndMonth) throws BaseException {
        try {
            return legacyDiaryDao.selectIsDiaryInMonth(userId, yearAndMonth);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.USERS_DIARY_NOT_EXISTS);
        }
    }

    public List<GetStickerRes> retrieveStickerListByDiary(Long diaryId) throws BaseException {
        try {
            return legacyDiaryDao.selectStickerListByDate(diaryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    public Long retrieveDiaryIdByDate(Long userId, String createdDate) throws BaseException {
        assertUsersDiaryValidByDate(userId, createdDate);
        try{
            return legacyDiaryDao.selectDiaryIdByDate(userId, createdDate);
        }catch(Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}

