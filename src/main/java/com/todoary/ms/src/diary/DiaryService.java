package com.todoary.ms.src.diary;


import com.todoary.ms.src.diary.dto.PostDiaryReq;
import com.todoary.ms.src.user.UserProvider;
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
    private final UserProvider userProvider;

    @Autowired
    public DiaryService(DiaryProvider diaryProvider, DiaryDao diaryDao, UserProvider userProvider) {
        this.diaryProvider = diaryProvider;
        this.diaryDao = diaryDao;
        this.userProvider=userProvider;
    }

    public void createOrModifyDiary(Long userId, PostDiaryReq postDiaryReq, String createdDate) throws BaseException {
        try {
            diaryDao.insertOrUpdateDiary(userId, postDiaryReq, createdDate);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }



    public void removeDiary(Long userId, String createdDate) throws BaseException {
        diaryProvider.assertUsersDiaryValidByDate(userId, createdDate);
        try {
            diaryDao.deleteDiary(userId, createdDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }




}
