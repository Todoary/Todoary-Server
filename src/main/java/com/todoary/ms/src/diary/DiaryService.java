package com.todoary.ms.src.diary;


import com.todoary.ms.src.diary.dto.PostDiaryReq;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void createOrModifyDiary(long userId, PostDiaryReq postDiaryReq) throws BaseException {
        try {
            diaryDao.insertOrUpdateDiary(userId, postDiaryReq);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }



    public void removeDiary(long userId, String createdDate) throws BaseException {
        diaryProvider.assertUsersDiaryValidByDate(userId, createdDate);
        try {
            diaryDao.deleteDiary(userId, createdDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
