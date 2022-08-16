package com.todoary.ms.src.sticker;

import com.todoary.ms.src.diary.DiaryProvider;
import com.todoary.ms.src.sticker.dto.GetStickerRes;
import com.todoary.ms.src.todo.dto.GetTodoByCategoryRes;
import com.todoary.ms.src.todo.dto.GetTodoByDateRes;
import com.todoary.ms.src.user.UserProvider;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.ErrorLogWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static com.todoary.ms.util.ErrorLogWriter.writeExceptionWithAuthorizedRequest;

@Slf4j
@RestController
@RequestMapping("/sticker")
public class StickerController {

    private final StickerProvider stickerProvider;
    private final DiaryProvider diaryProvider;

    @Autowired
    public StickerController(StickerProvider stickerProvider, DiaryProvider diaryProvider) {
        this.stickerProvider = stickerProvider;
        this.diaryProvider = diaryProvider;
    }

    private Long getDiaryIdFromRequest(HttpServletRequest request) throws BaseException {
        Long diaryId = Long.parseLong(request.getAttribute("diary_id").toString());
        String createdDate= request.getAttribute("created_date").toString();
        diaryProvider.assertUsersDiaryValidByDate(diaryId, createdDate);
        return diaryId;
    }


    /**
     * 6.1 스티커 조회 api
     */
    @GetMapping(value = "", params = "createdDate")
    public BaseResponse<List<GetStickerRes>> getDiaryListByDate(HttpServletRequest request,
                                                          @RequestParam("createdDate") String created_at) {
        try {
            Long diaryId = getDiaryIdFromRequest(request);
            return new BaseResponse<>(stickerProvider.retrieveStickerListByDate(diaryId, created_at));
        } catch (BaseException e) {
            log.warn(e.getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }



}