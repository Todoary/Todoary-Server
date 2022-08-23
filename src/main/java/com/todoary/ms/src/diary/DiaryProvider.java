package com.todoary.ms.src.diary;

import com.todoary.ms.src.diary.dto.GetDiaryByDateRes;
import com.todoary.ms.src.diary.dto.GetStickerRes;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DiaryProvider {

    private final DiaryDao diaryDao;


    @Autowired
    public DiaryProvider(DiaryDao diaryDao) {
        this.diaryDao = diaryDao;
    }

    public boolean checkUsersDiaryById(Long userId, String createdDate) throws BaseException {
        try {
            return (diaryDao.selectExistsUsersDiaryByDate(userId, createdDate) == 1);
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
            return diaryDao.selectDiaryByDate(diaryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<Integer> retrieveIsDiaryInMonth(Long userId, String yearAndMonth) throws BaseException {
        try {
            return diaryDao.selectIsDiaryInMonth(userId, yearAndMonth);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.USERS_DIARY_NOT_EXISTS);
        }
    }

    public List<GetStickerRes> retrieveStickerListByDiary(Long diaryId) throws BaseException {
        try {
            return diaryDao.selectStickerListByDate(diaryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    public Long retrieveDiaryIdByDate(Long userId, String createdDate) throws BaseException {
        assertUsersDiaryValidByDate(userId, createdDate);
        try{
            return diaryDao.selectDiaryIdByDate(userId, createdDate);
        }catch(Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}

