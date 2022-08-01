package com.todoary.ms.src.diary;

import com.todoary.ms.src.diary.dto.GetDiaryByDateRes;
import com.todoary.ms.src.diary.dto.PostDiaryReq;
import com.todoary.ms.src.diary.dto.PostDiaryRes;
import com.todoary.ms.src.user.UserProvider;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    @PostMapping("")
    public BaseResponse<BaseResponseStatus> postDiary(HttpServletRequest request, @RequestBody PostDiaryReq postDiaryReq, @PathVariable("createdDate") String createdDate) {
        try {
            Long userId = getUserIdFromRequest(request);
            diaryService.createOrModifyDiary(userId, postDiaryReq, createdDate);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            log.warn(e.getMessage());
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
            log.warn(e.getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 5.3 일기 조회 api
     */
    @GetMapping(value = "", params = "createdDate")
    public BaseResponse<GetDiaryByDateRes> getDiaryListByDate(HttpServletRequest request,
                                                                   @RequestParam("createdDate") String created_at) {
        try {
            Long userId = getUserIdFromRequest(request);
            return new BaseResponse<>(diaryProvider.retrieveDiaryByDate(userId, created_at));
        } catch (BaseException e) {
            log.warn(e.getMessage());
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
            log.warn(e.getMessage());
            return new BaseResponse<>(e.getStatus());
        }
    }

}
