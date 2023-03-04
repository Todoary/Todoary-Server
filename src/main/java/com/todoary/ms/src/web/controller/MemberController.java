package com.todoary.ms.src.web.controller;


import com.todoary.ms.src.common.auth.annotation.LoginMember;
import com.todoary.ms.src.common.response.BaseResponse;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.s3.AwsS3Service;
import com.todoary.ms.src.service.MemberService;
import com.todoary.ms.src.web.dto.*;
import com.todoary.ms.src.web.dto.alarm.AlarmEnablesResponse;
import com.todoary.ms.src.web.dto.alarm.DailyAlarmEnablesRequest;
import com.todoary.ms.src.web.dto.alarm.RemindAlarmEnablesRequest;
import com.todoary.ms.src.web.dto.alarm.TodoAlarmEnablesRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static com.todoary.ms.src.common.response.BaseResponseStatus.SUCCESS;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final AwsS3Service awsS3Service;

    // 2.1 닉네임 및 한줄소개 변경 api
    @PatchMapping("/profile")
    public BaseResponse<MemberProfileUpdateResponse> patchProfile(
            @LoginMember Long memberId,
            @RequestBody MemberProfileUpdateRequest request
    ) {
        String newIntroduce = request.getIntroduce();
        String newNickname = request.getNickname();

        memberService.updateProfile(memberId, new MemberProfileParam(newIntroduce, newNickname));

        return new BaseResponse(new MemberProfileUpdateResponse(newIntroduce, newNickname));
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
    @PatchMapping("/profile-img/default")
    public BaseResponse<String> resetProfileImg(@LoginMember Long memberId) {
        if (memberService.checkProfileImgIsDefault(memberId)) {
            return new BaseResponse<>("삭제에 성공했습니다.");
        }

        memberService.setProfileImgDefault(memberId);
        return new BaseResponse<>("삭제에 성공했습니다.");
    }

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
        memberService.deactivateMember(memberId);
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
            @RequestBody TodoAlarmEnablesRequest request
    ) {
        memberService.activeTodoAlarm(memberId, request.isChecked());
        return BaseResponse.from(SUCCESS);
    }

    // 2.7.2 하루기록 알림 활성화 api
    @PatchMapping("/alarm/diary")
    public BaseResponse<BaseResponseStatus> patchDailyAlarmStatus(
            @LoginMember Long memberId,
            @RequestBody DailyAlarmEnablesRequest request
    ) {
        memberService.activeDailyAlarm(memberId, request.isChecked());
        return BaseResponse.from(SUCCESS);
    }

    // 2.7.3 리마인드 알림 활성화 api
    @PatchMapping("/alarm/remind")
    public BaseResponse<BaseResponseStatus> patchRemindAlarmStatus(
            @LoginMember Long memberId,
            @RequestBody RemindAlarmEnablesRequest request
    ) {
        memberService.activeRemindAlarm(memberId, request.isChecked());
        return BaseResponse.from(SUCCESS);
    }

    // 2.8 알림 활성화 여부 조회 api
    @GetMapping("/alarm")
    public BaseResponse<AlarmEnablesResponse> getAlarmEnabled(@LoginMember Long memberId) {
        Member member = memberService.findById(memberId);
        return new BaseResponse<>(AlarmEnablesResponse.builder()
                                          .memberId(memberId)
                                          .toDoAlarmEnable(member.getToDoAlarmEnable())
                                          .dailyAlarmEnable(member.getDailyAlarmEnable())
                                          .remindAlarmEnable(member.getRemindAlarmEnable())
                                          .build());
    }


    // 2.9 마케팅동의 api
    @PatchMapping("/service/terms")
    public BaseResponse<BaseResponseStatus> patchTermsStatus(
            @LoginMember Long memberId,
            @RequestBody TermsEnablesRequest request
    ) {
        memberService.activeTermsStatus(memberId, request.isChecked());
        return BaseResponse.from(SUCCESS);
    }

    /**
     * 2.10 FCM 토큰 갱신 api
     */
    @PatchMapping("/fcm_token")
    public BaseResponse<String> modifyFcmToken(
            @LoginMember Long memberId,
            @RequestBody @Valid FcmTokenUpdateRequest request
    ) {
        String fcmToken = request.getFcmToken();
        memberService.modifyFcmToken(memberId, fcmToken);

        return new BaseResponse("FCM 토큰 갱신에 성공했습니다.");
    }

//    @ToString
//    @Getter
//    @NoArgsConstructor(access = AccessLevel.PROTECTED)
//    @AllArgsConstructor
//    @Builder
//    public static class AlarmRequest {
//        @NotNull(message = "NULL_ARGUMENT")
//        private Long memberId;
//        @NotNull(message = "NULL_ARGUMENT")
//        private Boolean toDoAlarmEnable;
//        @NotNull(message = "NULL_ARGUMENT")
//        private Boolean remindAlarmEnable;
//        @NotNull(message = "NULL_ARGUMENT")
//        private Boolean dailyAlarmEnable;
//    }
//
//    @ToString
//    @Getter
//    @NoArgsConstructor(access = AccessLevel.PROTECTED)
//    @AllArgsConstructor
//    @Builder
//    public static class TermsRequest {
//        @NotNull(message = "NULL_ARGUMENT")
//        private Long memberId;
//        @NotNull(message = "NULL_ARGUMENT")
//        private Boolean isTermsEnable;
//    }
}
