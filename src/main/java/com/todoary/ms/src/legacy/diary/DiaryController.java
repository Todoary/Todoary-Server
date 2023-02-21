package com.todoary.ms.src.legacy.diary;


import com.todoary.ms.src.diary.dto.*;
import com.todoary.ms.src.legacy.diary.dto.GetDiaryByDateRes;
import com.todoary.ms.src.legacy.diary.dto.GetStickerRes;
import com.todoary.ms.src.legacy.diary.dto.PostDiaryReq;
import com.todoary.ms.src.legacy.diary.dto.PutStickersReq;
import com.todoary.ms.src.legacy.user.UserProvider;
import com.todoary.ms.src.legacy.BaseException;
import com.todoary.ms.src.common.response.BaseResponse;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import com.todoary.ms.src.common.util.ColumnLengthInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.todoary.ms.src.common.util.ErrorLogWriter.writeExceptionWithAuthorizedRequest;

@Slf4j
@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryProvider diaryProvider;
    private final DiaryService diaryService;
    private final UserProvider userProvider;

    @Autowired
    public DiaryController(DiaryProvider diaryProvider, DiaryService diaryService, UserProvider userProvider) {
        this.diaryProvider = diaryProvider;
        this.diaryService = diaryService;
        this.userProvider = userProvider;
    }

    private Long getUserIdFromRequest(HttpServletRequest request) throws BaseException {
        Long userId = Long.parseLong(request.getAttribute("user_id").toString());
        userProvider.assertUserValidById(userId);
        return userId;
    }


    /**
     * 5.1 일기 생성/수정 api
     */
    @PostMapping("/{createdDate}")
    public BaseResponse<BaseResponseStatus> postDiary(HttpServletRequest request, @RequestBody PostDiaryReq postDiaryReq, @PathVariable("createdDate") String createdDate) {
        if (ColumnLengthInfo.getGraphemeLength(postDiaryReq.getTitle()) > ColumnLengthInfo.DIARY_TITLE_MAX_LENGTH)
            return new BaseResponse<>(BaseResponseStatus.DATA_TOO_LONG);
        try {
            Long userId = getUserIdFromRequest(request);
            diaryService.createOrModifyDiary(userId, postDiaryReq, createdDate);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request, postDiaryReq.toString());
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 5.2 일기 삭제 api
     */
    @DeleteMapping("/{createdDate}")
    public BaseResponse<BaseResponseStatus> deleteDiaryById(HttpServletRequest request, @PathVariable("createdDate") String createdDate) {
        try {
            Long userId = getUserIdFromRequest(request);
            diaryService.removeDiary(userId, createdDate);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request);
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 5.3 일기 조회 api
     */
    @GetMapping(value = "", params = "createdDate")
    public BaseResponse<GetDiaryByDateRes> getDiaryListByDate(HttpServletRequest request,
                                                              @RequestParam("createdDate") String createdDate) {
        try {
            Long userId = getUserIdFromRequest(request);
            Long diaryId = diaryProvider.retrieveDiaryIdByDate(userId, createdDate);
            return new BaseResponse<>(diaryProvider.retrieveDiaryByDate(diaryId));
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request);
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 5.4 월별 일기 존재 여부 조회 api
     */
    @GetMapping("/days/{yearAndMonth}")
    public BaseResponse<List<Integer>> getDiaryInMonth(HttpServletRequest request,
                                                       @PathVariable("yearAndMonth") String yearAndMonth) {
        try {
            Long userId = getUserIdFromRequest(request);
            return new BaseResponse<>(diaryProvider.retrieveIsDiaryInMonth(userId, yearAndMonth));
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request);
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 5.5 일기 스티커 생성/수정/삭제 api
     */
    @PutMapping("/{createdDate}/sticker")
    public BaseResponse<List<Long>> patchStickers(HttpServletRequest request,
                                                  @PathVariable("createdDate") String createdDate,
                                                  @RequestBody PutStickersReq putStickersReq) {
        try {
            Long userId = getUserIdFromRequest(request);
            Long diaryId = diaryProvider.retrieveDiaryIdByDate(userId, createdDate);
            List<Long> createdIds = null;
            if (putStickersReq.getCreated() != null && !putStickersReq.getCreated().isEmpty()) {
                createdIds = diaryService.createStickers(diaryId, putStickersReq.getCreated());
            }
            if (putStickersReq.getModified() != null && !putStickersReq.getModified().isEmpty()) {
                diaryService.modifyStickers(putStickersReq.getModified());
            }
            if (putStickersReq.getDeleted() != null && !putStickersReq.getDeleted().isEmpty()) {
                diaryService.removeStickers(putStickersReq.getDeleted());
            }
            return new BaseResponse<>(createdIds);
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request, putStickersReq.toString());
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 5.6 일기 스티커 조회 api
     */
    @GetMapping("/{createdDate}/sticker")
    public BaseResponse<List<GetStickerRes>> getStickerListByDiary(HttpServletRequest request,
                                                                   @PathVariable("createdDate") String createdDate) {
        try {
            Long userId = getUserIdFromRequest(request);
            Long diaryId = diaryProvider.retrieveDiaryIdByDate(userId, createdDate);
            return new BaseResponse<>(diaryProvider.retrieveStickerListByDiary(diaryId));
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request);
            return new BaseResponse<>(e.getStatus());
        }
    }
}
