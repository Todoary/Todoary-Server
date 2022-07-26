package com.todoary.ms.src.diary;

import com.todoary.ms.src.diary.DiaryDao;
import com.todoary.ms.src.diary.dto.GetDiaryByDateRes;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DiaryProvider {

    private final DiaryDao diaryDao;


    @Autowired
    public DiaryProvider(DiaryDao diaryDao) {
        this.diaryDao = diaryDao;
    }

    public boolean checkUsersDiaryById(long userId, long diaryId) throws BaseException {
        try {
            return (diaryDao.selectExistsUsersDiaryById(userId, diaryId) == 1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void assertUsersDiaryValidById(long userId, long diaryId) throws BaseException {
        if (!checkUsersDiaryById(userId, diaryId))
            throw new BaseException(BaseResponseStatus.USERS_DIARY_NOT_EXISTS);
    }

    public List<GetDiaryByDateRes> retrieveDiaryListByDate(long userId, String created_at) throws BaseException {
        try {
            return diaryDao.selectDiaryListByDate(userId, created_at);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
