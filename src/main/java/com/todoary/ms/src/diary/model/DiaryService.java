package com.todoary.ms.src.diary.model;


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

    public long createDiary(long userId, PostDiaryReq postDiaryReq) throws BaseException {
        try {
            long todoId = diaryDao.insertDiary(userId, postDiaryReq.getTitle(), postDiaryReq.getTargetDate());
            return todoId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void modifyDiary(long userId, long diaryId, PostDiaryReq postDiaryReq) throws BaseException {
        diaryProvider.assertUsersDiaryValidById(userId, diaryId);
        try {
            diaryDao.updateDiary(userId, diaryId, postDiaryReq);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    public void removeDiary(long userId, long diaryId) throws BaseException {
        diaryProvider.assertUsersDiaryValidById(userId, diaryId);
        try {
            diaryDao.deleteDiary(diaryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
