package com.todoary.ms.src.service;


import com.todoary.ms.src.domain.Diary;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.repository.DiaryRepository;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.web.dto.diary.DiaryRequest;
import com.todoary.ms.src.web.dto.diary.DiaryResponse;
import org.assertj.core.api.SoftAssertions;
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
public class DiaryServiceTest {

    @Autowired
    EntityManager em;

    @MockBean
    MemberRepository memberRepository;

    @Autowired
    DiaryService diaryService;

    @Autowired
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
        //when
        diaryService.saveDiaryOrUpdate(member.getId(), request, date);
        //then
        Optional<Diary> diary = diaryRepository.findByMemberAndDate(member, date);
        assertThat(diary).isNotEmpty();
        assertThat(diary.get().getTitle()).isEqualTo("title");
        assertThat(diary.get().getContent()).isEqualTo("content");
    }

    @Test
    void Diary_수정() {
        // given
        Member member = createMember();
        DiaryRequest request = DiaryRequest.builder()
                .title("title")
                .content("content")
                .build();
        diaryService.saveDiaryOrUpdate(member.getId(), request, LocalDate.of(2023, 1, 1));
        // when
        String title = "title2";
        String content="content2";
        diaryService.saveDiaryOrUpdate(member.getId(), new DiaryRequest(title, content), LocalDate.of(2023, 1, 1));
        // then
        Optional<Diary> diary = diaryRepository.findByMemberAndDate(member, LocalDate.of(2023, 1, 1));
        assertThat(diary).isNotEmpty();
        assertThat(diary.get().getTitle()).isEqualTo("title2");
        assertThat(diary.get().getContent()).isEqualTo("content2");
    }


    @Test
    void Diary_삭제() {
        // given
        Member member = createMember();
        DiaryRequest request = DiaryRequest.builder()
                .title("title")
                .content("content")
                .build();
        diaryService.saveDiaryOrUpdate(member.getId(), request, LocalDate.of(2023, 1, 1));
        // when
        diaryService.deleteDiary(member.getId(), LocalDate.of(2023, 1, 1));
        // then
        assertThat(diaryRepository.findByMemberAndDate(member, LocalDate.of(2023, 1, 1))).isEmpty();
        assertThat(member.getDiaries()).hasSize(0);
    }


    @Test
    void 존재하는_Diary_조회() {
        // given
        Member member = createMember();
        DiaryRequest request = new DiaryRequest("title", "content");
        LocalDate createdDate = LocalDate.of(2023, 1, 1);
        diaryService.saveDiaryOrUpdate(member.getId(), request, createdDate);
        // when
        DiaryResponse diary = diaryService.retrieveDiaryByDate(createdDate, member.getId());
        // then
        assertThat(diary.getTitle()).isEqualTo(request.getTitle());
        assertThat(diary.getContent()).isEqualTo(request.getContent());
    }

    @Test
    void 존재하지않는_Diary_조회시_null응답() {
        // given
        Member member = createMember();
        // when
        DiaryResponse diary = diaryService.retrieveDiaryByDate(LocalDate.now(), member.getId());
        // then
        assertThat(diary).isNull();
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
                LocalDate.of(2023, 2, 4),
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
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(january).hasSize(2);
            softly.assertThat(february).hasSize(3);
            softly.assertThat(march).hasSize(1);
        });
    }



    Member createMember() {
        Member member = Member.builder().email("email").build();
        em.persist(member);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        return member;
    }
}
