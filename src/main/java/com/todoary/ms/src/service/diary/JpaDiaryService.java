package com.todoary.ms.src.service.diary;


import com.todoary.ms.src.domain.Diary;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.exception.common.TodoaryException;
import com.todoary.ms.src.repository.DiaryRepository;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.web.dto.DiaryRequest;
import com.todoary.ms.src.web.dto.DiaryResponse;
import com.todoary.ms.src.web.dto.StickerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import static com.todoary.ms.util.BaseResponseStatus.USERS_DIARY_NOT_EXISTS;
import static com.todoary.ms.util.BaseResponseStatus.USERS_EMPTY_USER_ID;

@RequiredArgsConstructor
@Service
public class JpaDiaryService
{


    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;

    private final StickerStartingTodayCondition stickerByDairyCondition;



    @Transactional
    public void createOrModify(Long memberId, DiaryRequest request, LocalDate createdDate) {
        Member member = findMemberById(memberId);
        Diary diary = findDiaryByDate(createdDate, member);
        diary.update(
                request.getTitle(),
                request.getContent()
        );
    }

    @Transactional
    public void deleteDiary(Long memberId, LocalDate createdDate) {
        Member member = findMemberById(memberId);
        Diary diary = findDiaryByDate(createdDate, member);
        diary.removeAssociations();
        diaryRepository.delete(diary);
    }

    @Transactional(readOnly = true)
    public DiaryResponse findDiary(LocalDate createdDate, Long memberId) {
        Member member = findMemberById(memberId);
        Diary diary = diaryRepository.findByDate(createdDate)
                .orElseThrow(() -> new TodoaryException(USERS_DIARY_NOT_EXISTS));
        if (!diary.has(member))
            throw new TodoaryException(USERS_DIARY_NOT_EXISTS);
        return (DiaryResponse) member.getDiary()
                .stream().map(d -> new DiaryResponse(
                d.getId(), d.getTitle(), d.getContent(), d.getCreatedDate())
        );
    }


    @Transactional(readOnly = true)
    public List<Integer> findDiaryInMonth(Long memberId, YearMonth yearMonth) {
        Member member = findMemberById(memberId);

        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        return diaryRepository.findBetweenDaysAndMember(firstDay, lastDay, member)
                .stream().map(diary -> diary.getCreatedDate().getDayOfMonth())
                .distinct()
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Diary findDiaryByDate(LocalDate createdDate, Member member) {
        Diary diary = diaryRepository.findByDate(createdDate)
                .orElseThrow(() -> new TodoaryException(USERS_DIARY_NOT_EXISTS));
        if (!diary.has(member))
            throw new TodoaryException(USERS_DIARY_NOT_EXISTS);
        return diary;
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new TodoaryException(USERS_EMPTY_USER_ID));
    }

    @Transactional(readOnly = true)
    public List<StickerResponse> findStickersByDiary(Long memberId, LocalDate createdDate) {
        Member member = findMemberById(memberId);
        Diary diary = findDiaryByDate(createdDate, member);
        return diaryRepository.findStickersByDiary(diary, stickerByDairyCondition.getPredicate())
                .stream().map(StickerResponse::from).collect(Collectors.toList());
    }

}
