package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.web.dto.*;
import com.todoary.ms.src.web.dto.category.CategoryRequest;
import com.todoary.ms.src.web.dto.category.CategoryResponse;
import com.todoary.ms.src.web.dto.category.CategorySaveResponse;
import com.todoary.ms.src.web.dto.diary.DiaryRequest;
import com.todoary.ms.src.web.dto.diary.DiaryResponse;
import com.todoary.ms.src.web.dto.diary.StickerRequest;
import com.todoary.ms.src.web.dto.diary.StickersRequest;
import com.todoary.ms.src.web.dto.todo.TodoRequest;
import com.todoary.ms.src.web.dto.todo.TodoSaveResponse;
import com.todoary.ms.util.BaseResponse;
import lombok.*;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

import static com.todoary.ms.util.ColumnLengthInfo.CATEGORY_TITLE_MAX_LENGTH;
import static com.todoary.ms.util.ColumnLengthInfo.TODO_TITLE_MAX_LENGTH;

@RequiredArgsConstructor
@RestController
@Profile({"dev", "local-rds", "local-h2"})
@RequestMapping("/jpa/test")
public class JpaTestController {

    private final JpaAuthController authController;
    private final JpaTodoController todoController;
    private final JpaCategoryController categoryController;
    private final JwtTokenProvider jwtTokenProvider;

    private final JpaDiaryController diaryController;

    @GetMapping("")
    public BaseResponse<TestMemberDto> joinTestMember() {
        TestMemberDto testMember = saveRandomMember();
        saveRandomCategory(testMember);
        saveRandomTodo(testMember);
        saveRandomDiary(testMember);
        saveRandomSticker(testMember);
        return new BaseResponse<>(testMember);
    }

    private TestMemberDto saveRandomMember() {
        MemberJoinRequest member = generateMemberRequest();
        authController.joinNormalMember(member);
        SigninResponse token = authController.login(new SigninRequest(member.getEmail(), member.getPassword())).getResult();
        Long memberId = Long.parseLong(jwtTokenProvider.getUserIdFromAccessToken(token.getAccessToken()));
        return new TestMemberDto(memberId, member, token);
    }

    private void saveRandomCategory(TestMemberDto testMember) {
        CategoryRequest request = new CategoryRequest(generateNumeralOrLetters(CATEGORY_TITLE_MAX_LENGTH), new Random().nextInt(10) + 1);
        CategorySaveResponse response = categoryController.createCategory(testMember.memberId, request).getResult();
        testMember.categoryId = response.getCategoryId();
        testMember.category = categoryController.retrieveCategory(testMember.memberId).getResult();
    }

    private void saveRandomTodo(TestMemberDto testMember) {
        TodoRequest request = TodoRequest.builder()
                .title(generateNumeralOrLetters(TODO_TITLE_MAX_LENGTH))
                .targetTime(LocalTime.now())
                .targetDate(LocalDate.now())
                .categoryId(testMember.categoryId)
                .isAlarmEnabled(true)
                .build();
        TodoSaveResponse response = todoController.createTodo(testMember.memberId, request).getResult();
        testMember.todoId = response.getTodoId();
        testMember.todo = request;
    }

    private void saveRandomDiary(TestMemberDto testMember) {
        DiaryRequest request = DiaryRequest.builder()
                .content("기본 일기 내용")
                .title("기본 일기 제목")
                .build();
        LocalDate now = LocalDate.now();
        diaryController.createDiary(testMember.memberId, request, now);
        testMember.diary = diaryController.retrieveDiary(testMember.getMemberId(), now).getResult();
    }

    private void saveRandomSticker(TestMemberDto testMember) {
        StickerRequest stickerRequest = StickerRequest.builder()
                .stickerId(1)
                .locationX(10.5)
                .locationY(12.5)
                .width(13.5)
                .height(19.3)
                .rotation(20.5)
                .flipped(false)
                .build();
        List<StickerRequest> created = List.of(stickerRequest, stickerRequest);
        List<Long> createdStickers =
                diaryController.updateStickersInDiaryOnDate(
                        testMember.memberId, testMember.diary.getCreatedDate(),
                        new StickersRequest(created, null, null)
                ).getResult();
        testMember.stickerIds = createdStickers;
        testMember.stickers = created;
    }

    private MemberJoinRequest generateMemberRequest() {
        int maxNicknameLength = 10;
        String nickname = generateNumeralOrLetters(maxNicknameLength);
        return MemberJoinRequest.builder()
                .name(nickname)
                .nickname(nickname)
                .email(nickname + "@gmail.com")
                .password("1234")
                .isTermsEnable(true)
                .build();
    }

    private String generateNumeralOrLetters(int length) {
        // 아스키 코드 48 ~ 122까지 랜덤 문자
        // 예: qOji6mPStx
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)) // 아스키코드 숫자 알파벳 중간에 섞여있는 문자들 제거
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @NoArgsConstructor
    @Getter
    @ToString
    private static class TestMemberDto {
        public DiaryResponse diary;
        public List<Long> stickerIds;
        public List<StickerRequest> stickers;
        private SigninResponse token;
        private Long memberId;
        private Long categoryId;
        private Long todoId;
        private MemberJoinRequest member;
        private CategoryResponse[] category;
        private TodoRequest todo;

        public TestMemberDto(Long memberId, MemberJoinRequest member, SigninResponse token) {
            this.memberId = memberId;
            this.member = member;
            this.token = token;
        }
    }
}
