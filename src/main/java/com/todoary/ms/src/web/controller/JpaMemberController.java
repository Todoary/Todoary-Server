package com.todoary.ms.src.web.controller;


import com.todoary.ms.src.config.auth.LoginMember;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.s3.AwsS3Service;
import com.todoary.ms.src.service.MemberService;
import com.todoary.ms.src.web.dto.*;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.todoary.ms.util.BaseResponseStatus.SUCCESS;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/jpa/member")
public class JpaMemberController {
    private final MemberService memberService;
    private final AwsS3Service awsS3Service;

    // 2.1 닉네임 및 한줄소개 변경 api
    @PatchMapping("/profile")
    public BaseResponse<BaseResponseStatus> patchProfile(
            @LoginMember Long memberId,
            @RequestBody @Valid MemberProfileRequest request
    ) {
        memberService.updateProfile(memberId, request);
        return BaseResponse.from(SUCCESS);
    }


    // 2.2 프로필 사진 수정 api
    @PatchMapping("/profile-img")
    public BaseResponse<MemberProfileImgUrlResponse> uploadProfileImg(
            @LoginMember Long memberId,
            @RequestPart("profile-img") MultipartFile multipartFile
    ) {
        String memberProfileImgUrl = memberService.getProfileImgUrlById(memberId);
        awsS3Service.fileDelete(memberProfileImgUrl);
        String newProfileImgUrl = awsS3Service.upload(multipartFile, memberId);
        memberService.changeProfileImg(memberId, newProfileImgUrl);
        return new BaseResponse<>(new MemberProfileImgUrlResponse(memberId, newProfileImgUrl));
    }

    // 2.3 프로필 사진 삭제 api

    /**
     * API 2.3은 멤버 회원 탈퇴 시에 AWS S3에 저장된 프로필 사진을 삭제하기 위함.
     * 따라서 별도의 API 대신 memberService에 로직 추가하는 것으로 대체.
     */

    // 2.4 프로필 조회 api
    @GetMapping("")
    public BaseResponse<MemberResponse> retrieveMemberProfile(
            @LoginMember Long memberId) {
        return new BaseResponse<>(memberService.findMemberProfile(memberId));
    }

    // 2.5 유저 삭제 api
    @PatchMapping("/status")
    public BaseResponse<BaseResponseStatus> patchMemberStatus(
            @LoginMember Long memberId
    ) {
        memberService.removeMember(memberId);
        return BaseResponse.from(SUCCESS);
    }

    // 2.6 로그아웃 api
    @PostMapping("/signout")
    public BaseResponse<BaseResponseStatus> logout(@LoginMember Long memberId) {
        memberService.removeTokens(memberId);
        return BaseResponse.from(SUCCESS);
    }

    // 2.7.1 Todoary 알림 활성화 api
    @PatchMapping("/alarm/todo")
    public BaseResponse<BaseResponseStatus> patchTodoAlarmStatus(
            @LoginMember Long memberId,
            @RequestBody @Valid JpaMemberController.AlarmRequest request
    ) {
        memberService.activeTodoAlarm(memberId, request.getToDoAlarmEnable());
        return BaseResponse.from(SUCCESS);
    }

    // 2.7.2 하루기록 알림 활성화 api
    @PatchMapping("/alarm/diary")
    public BaseResponse<BaseResponseStatus> patchDailyAlarmStatus(
            @LoginMember Long memberId,
            @RequestBody @Valid JpaMemberController.AlarmRequest request
    ) {
        memberService.activeDailyAlarm(memberId, request.getDailyAlarmEnable());
        return BaseResponse.from(SUCCESS);
    }

    // 2.7.3 리마인드 알림 활성화 api
    @PatchMapping("/alarm/remind")
    public BaseResponse<BaseResponseStatus> patchRemindAlarmStatus(
            @LoginMember Long memberId,
            @RequestBody @Valid JpaMemberController.AlarmRequest request
    ) {
        memberService.activeRemindAlarm(memberId, request.getRemindAlarmEnable());
        return BaseResponse.from(SUCCESS);
    }

    // 2.8 알림 활성화 여부 조회 api
    @GetMapping("/alarm")
    public BaseResponse<AlarmEnablesResponse> getAlarmEnabled(@LoginMember Long memberId) {
        Member member = memberService.findById(memberId);

        return new BaseResponse<>(new AlarmEnablesResponse(
                member.getId(),
                member.getToDoAlarmEnable(),
                member.getDailyAlarmEnable(),
                member.getRemindAlarmEnable()
        ));
    }


    // 2.9 마케팅동의 api
    @PatchMapping("/service/terms")
    public BaseResponse<BaseResponseStatus> patchTermsStatus(
            @LoginMember Long memberId,
            @RequestBody @Valid JpaMemberController.TermsRequest request
    ) {
        memberService.activeTermsStatus(memberId, request.getIsTermsEnable());
        return BaseResponse.from(SUCCESS);
    }

    @ToString
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class AlarmRequest {
        @NotNull(message = "NULL_ARGUMENT")
        private Long memberId;
        @NotNull(message = "NULL_ARGUMENT")
        private Boolean toDoAlarmEnable;
        @NotNull(message = "NULL_ARGUMENT")
        private Boolean remindAlarmEnable;
        @NotNull(message = "NULL_ARGUMENT")
        private Boolean dailyAlarmEnable;
    }

    @ToString
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class TermsRequest {
        @NotNull(message = "NULL_ARGUMENT")
        private Long memberId;
        @NotNull(message = "NULL_ARGUMENT")
        private Boolean isTermsEnable;
    }
}
