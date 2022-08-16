package com.todoary.ms.src.sticker;

import com.todoary.ms.src.sticker.dto.GetStickerRes;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StickerProvider {

    private final StickerDao stickerDao;


    @Autowired
    public StickerProvider(StickerDao stickerDao) {
        this.stickerDao = stickerDao;
    }


    public List<GetStickerRes> retrieveStickerListByDate(Long diaryId, String created_date) throws BaseException {
        try {
            return stickerDao.selectStickerListByDate(diaryId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

}

