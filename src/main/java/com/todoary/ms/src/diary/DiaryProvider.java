package com.todoary.ms.src.diary;

import com.todoary.ms.src.diary.DiaryDao;
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
            return (diaryDao.selectExistsUsersDiaryById(userId, createdDate) == 1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.USERS_DIARY_NOT_EXISTS);
        }
    }

    public void assertUsersDiaryValidByDate(Long userId, String createdDate) throws BaseException {
        if (!checkUsersDiaryById(userId, createdDate))
            throw new BaseException(BaseResponseStatus.USERS_DIARY_NOT_EXISTS);
    }

    public GetDiaryByDateRes retrieveDiaryByDate(Long userId, String createdDate) throws BaseException {
        try {
            return diaryDao.selectDiaryByDate(userId, createdDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.USERS_DIARY_NOT_EXISTS);
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

    public List<GetStickerRes> retrieveStickerListByDiary(String createdDate) throws BaseException {
        try {
            return diaryDao.selectStickerListByDate(createdDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


}

