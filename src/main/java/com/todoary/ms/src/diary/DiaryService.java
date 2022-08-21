package com.todoary.ms.src.diary;


import com.todoary.ms.src.category.dto.PostCategoryReq;
import com.todoary.ms.src.diary.dto.PostDiaryReq;
import com.todoary.ms.src.diary.dto.PostStickerReq;
import com.todoary.ms.src.todo.dto.PostTodoReq;
import com.todoary.ms.src.user.UserProvider;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.todoary.ms.util.BaseResponseStatus.*;

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



    public void createSticker(String createdDate, PostStickerReq postStickerReq) throws BaseException {

        try {
            int diaryId=diaryDao.selectDiaryIdExist(createdDate);
            diaryDao.insertSticker(diaryId, postStickerReq);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void modifySticker(String createdDate, PostStickerReq postStickerReq) throws BaseException {
        try {
            int diaryId=diaryDao.selectDiaryIdExist(createdDate);
            diaryDao.updateSticker(diaryId,postStickerReq);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void removeSticker(Long diary_id, Long stickerId) throws BaseException {
        try {
            diaryDao.deleteSticker(stickerId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
