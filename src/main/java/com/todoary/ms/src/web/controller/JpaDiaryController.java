package com.todoary.ms.src.web.controller;


import com.todoary.ms.src.common.auth.annotation.LoginMember;
import com.todoary.ms.src.service.diary.JpaDiaryService;
import com.todoary.ms.src.web.dto.diary.DiaryRequest;
import com.todoary.ms.src.web.dto.diary.DiaryResponse;
import com.todoary.ms.src.web.dto.diary.StickerResponse;
import com.todoary.ms.src.web.dto.diary.StickersRequest;
import com.todoary.ms.src.common.response.BaseResponse;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static com.todoary.ms.src.common.response.BaseResponseStatus.SUCCESS;


@RestController
@RequiredArgsConstructor
@RequestMapping("/jpa/diary")
public class JpaDiaryController {

    private final JpaDiaryService diaryService;

    //5.1 일기 생성/수정 api
    @PostMapping("/{createdDate}")
    public BaseResponse<BaseResponseStatus> createDiary(
            @LoginMember Long memberId,
            @RequestBody @Valid DiaryRequest request,
            @PathVariable("createdDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdDate
    ) {
        diaryService.saveDiaryOrUpdate(memberId, request, createdDate);
        return BaseResponse.from(SUCCESS);
    }

    //5.2 일기 삭제 api
    @DeleteMapping("/{createdDate}")
    public BaseResponse<BaseResponseStatus> deleteDiaryById(
            @LoginMember Long memberId,
            @PathVariable("createdDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdDate
    ) {
        diaryService.deleteDiary(memberId, createdDate);
        return BaseResponse.from(SUCCESS);
    }

    //5.3 일기 조회 api
    @GetMapping(value = "", params = "createdDate")
    public BaseResponse<DiaryResponse> retrieveDiary(
            @LoginMember Long memberId,
            @RequestParam("createdDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdDate
    ) {
        return new BaseResponse<>(diaryService.findDiaryByDate(createdDate, memberId));
    }

    //5.4 월별 일기 존재 여부 조회 api
    @GetMapping("/days/{yearMonth}")
    public BaseResponse<List<Integer>> retrieveDiaryInMonth(
            @LoginMember Long memberId,
            @PathVariable("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth
    ) {
        List<Integer> days = diaryService.findDaysHavingDiaryInMonth(memberId, yearMonth);
        return new BaseResponse<>(days);
    }

    //5.5 일기 스티커 생성/수정/삭제 api
    @PutMapping("/{createdDate}/sticker")
    public BaseResponse<List<Long>> updateStickersInDiaryOnDate(
            @LoginMember Long memberId,
            @PathVariable("createdDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdDate,
            @RequestBody @Valid StickersRequest request) {
        List<Long> createdIds =
                diaryService.updateStickersInDiaryAndGetCreated(memberId, createdDate, request);
        return new BaseResponse<>(createdIds);
    }

    // 5.6 일기 스티커 조회 api
    @GetMapping("/{createdDate}/sticker")
    public BaseResponse<List<StickerResponse>> getStickerListByDiary(
            @LoginMember Long memberId,
            @PathVariable("createdDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdDate
    ) {
        List<StickerResponse> stickers = diaryService.findStickersInDiaryOnDate(memberId, createdDate);
        return new BaseResponse<>(stickers);
    }
}
