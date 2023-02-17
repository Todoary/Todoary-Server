package com.todoary.ms.src.service;


import com.todoary.ms.src.domain.Diary;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.exception.common.TodoaryException;
import com.todoary.ms.src.repository.DiaryRepository;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.web.dto.DiaryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import static com.todoary.ms.util.BaseResponseStatus.*;

@Service
public class JpaDiaryService
{


    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;


    @Autowired
    public JpaDiaryService(DiaryRepository diaryRepository, MemberRepository memberRepository) {
        this.diaryRepository = diaryRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Long createOrModify(Long memberId, LocalDate createdDate, DiaryRequest request) {
        Member member = findMemberById(memberId);
        Diary target = findDiaryByDate(createdDate, member);
        String nextTitle = request.getTitle();
        String nextContent = request.getContent();
        if (target.getCreatedDate().equals(createdDate)) {
            target.update(nextTitle,nextContent, createdDate);
            return memberId;
        }
        return diaryRepository.save(request.toEntity(member)).getId();
    }

    @Transactional
    public void deleteDiary(Long memberId, LocalDate createdDate) {
        Member member = findMemberById(memberId);
        Diary diary = findDiaryByDate(createdDate, member);
        diary.removeAssociations();
        diaryRepository.delete(diary);
    }

    @Transactional(readOnly = true)
    public Diary findDiary(LocalDate createdDate, Long memberId) {
        Member member = findMemberById(memberId);
        Diary diary = diaryRepository.findByDate(createdDate)
                .orElseThrow(() -> new TodoaryException(USERS_DIARY_NOT_EXISTS));
        if (!diary.has(member))
            throw new TodoaryException(USERS_DIARY_NOT_EXISTS);
        return diary;
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
}
