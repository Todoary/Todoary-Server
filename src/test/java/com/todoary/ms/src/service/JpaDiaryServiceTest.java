package com.todoary.ms.src.service;


import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.repository.DiaryRepository;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.service.diary.JpaDiaryService;
import com.todoary.ms.src.web.dto.diary.DiaryRequest;
import com.todoary.ms.src.web.dto.diary.DiaryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
public class JpaDiaryServiceTest {

    @Autowired
    EntityManager em;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    JpaDiaryService diaryService;

    @MockBean
    DiaryRepository diaryRepository;


    @Test
    void Diary_생성() {
        // given
        Member member = createMember();
        DiaryRequest request = DiaryRequest.builder()
                .title("title")
                .content("content")
                .build();
        LocalDate date = LocalDate.now();
        //then
        diaryService.saveDiaryOrUpdate(member.getId(), request, date);

    }

    @Test
    void Diary_수정() {
        // given
        Member member = createMember();
        DiaryRequest request = DiaryRequest.builder()
                .title("title")
                .content("content")
                .build();
        LocalDate firstDate = LocalDate.of(2023, 1, 1);
        diaryService.saveDiaryOrUpdate(member.getId(), request, firstDate);
        // when
        String title = "title2";
        String content="content2";
        LocalDate modifiedDate = LocalDate.now();
        diaryService.saveDiaryOrUpdate(member.getId(), new DiaryRequest(title, content), modifiedDate);

    }


    @Test
    void Diary_삭제() {
        // given
        Member member = createMember();
        DiaryRequest request = DiaryRequest.builder()
                .title("title")
                .content("content")
                .build();
        LocalDate createdDate = LocalDate.of(2023, 1, 1);
        // when
        diaryService.deleteDiary(member.getId(), createdDate);
        // then
        assertThat(diaryRepository.findByMemberAndDate(member, createdDate)).isEmpty();
        assertThat(member.getDiaries()).hasSize(0);
    }


    @Test
    void Diary_조회() {
        // given
        Member member = createMember();
        DiaryRequest request = new DiaryRequest("title", "content");
        LocalDate createdDate = LocalDate.of(2023, 1, 1);
        diaryService.saveDiaryOrUpdate(member.getId(), request, createdDate);
        // when
        DiaryResponse diary = diaryService.findDiaryByDate(createdDate, member.getId());
        // then
        //assertThat(diary.getTitle()).isEqualTo(request.getTitle());
        //assertThat(diary.getContent()).isEqualTo(request.getContent());

    }


    @Test
    void 특정_달의_Diary가_있는_날짜들_조회() {
        // given
        Member member = createMember();
        LocalDate[] dates = {
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 21),
                LocalDate.of(2023, 2, 14),
                LocalDate.of(2023, 2, 26),
                LocalDate.of(2023, 3, 4),
                LocalDate.of(2023, 3, 25),
        };
        Arrays.stream(dates)
                .forEach(date -> diaryService.saveDiaryOrUpdate(
                        member.getId(),
                        new DiaryRequest("todo", "content"), date
                ));
        // when
        List<Integer> january = diaryService.findDaysHavingDiaryInMonth(member.getId(), YearMonth.of(2023, 1));
        List<Integer> february = diaryService.findDaysHavingDiaryInMonth(member.getId(), YearMonth.of(2023, 2));
        List<Integer> march = diaryService.findDaysHavingDiaryInMonth(member.getId(), YearMonth.of(2023, 3));
        // then
        assertThat(january).hasSize(0);
        assertThat(february).hasSize(0);
        assertThat(march).hasSize(0);
    }



    Member createMember() {
        Member member = Member.builder().email("email").build();
        em.persist(member);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        return member;
    }
}
