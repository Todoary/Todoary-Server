package com.todoary.ms.src.web.controller;


import com.todoary.ms.src.config.auth.LoginMember;
import com.todoary.ms.src.service.diary.JpaDiaryService;
import com.todoary.ms.src.web.dto.DiaryRequest;
import com.todoary.ms.src.web.dto.DiaryResponse;
import com.todoary.ms.src.web.dto.StickerResponse;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static com.todoary.ms.util.BaseResponseStatus.SUCCESS;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/diary")
public class JpaDiaryController {

    private final JpaDiaryService diaryService;


    //5.1 일기 생성/수정 api
    @PostMapping("/{createdDate}")
    public BaseResponse<BaseResponseStatus> postDiary(
            @LoginMember Long memberId,
            @RequestBody @Valid DiaryRequest request,
            @PathVariable("createdDate") LocalDate createdDate
    ) {
        diaryService.createOrModify(memberId, request, createdDate);
        return BaseResponse.from(SUCCESS);
    }





    //5.2 일기 삭제 api
    @DeleteMapping("/{createdDate}")
    public BaseResponse<BaseResponseStatus> deleteDiaryById(
            @LoginMember Long memberId,
            @PathVariable("createdDate") LocalDate createdDate
    ) {
        diaryService.deleteDiary(memberId, createdDate);
        return BaseResponse.from(SUCCESS);
    }


    //5.3 일기 조회 api
    @GetMapping(value = "", params = "createdDate")
    public BaseResponse<DiaryResponse> retrieveDiary(
            @RequestParam(required = true) LocalDate createdDate,
            @LoginMember Long memberId
    ){
        return new BaseResponse<>(diaryService.findDiary(createdDate, memberId));
    }



    //5.4 월별 일기 존재 여부 조회 api
    @GetMapping("/days/{yearAndMonth}")
    public BaseResponse<List<Integer>> retrieveDiaryInMonth(
            @LoginMember Long memberId,
            @PathVariable("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth
    ) {
        List<Integer> days = diaryService.findDiaryInMonth(memberId, yearMonth);
        return new BaseResponse<>(days);
    }

/**
    //5.5 일기 스티커 생성/수정/삭제 api
    @PutMapping("/{createdDate}/sticker")
    public BaseResponse<List<Long>> patchStickers(
            @LoginMember Long memberId,
            @PathVariable("createdDate") String createdDate,
            @RequestBody @Valid StickerRequest request
    ) {

    }
*/

    // 5.6 일기 스티커 조회 api
    @GetMapping("/{createdDate}/sticker")
    public BaseResponse<List<StickerResponse>> getStickerListByDiary(
            @LoginMember Long memberId,
            @PathVariable("createdDate") String createdDate
    ) {
        List<StickerResponse> stickers = diaryService.findStickersByDiary(memberId, LocalDate.parse(createdDate));
        return new BaseResponse<>(stickers);
    }






}
