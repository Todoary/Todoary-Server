package com.todoary.ms.src.service;



import com.todoary.ms.src.domain.Diary;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.repository.DiaryRepository;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.web.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDate;
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
    void 일기_생성한다() {
        // given
        Member member = createMember();
        LocalDate createdDate = LocalDate.of(2022, 1, 1);
        String expectedTitle = "title";
        String expectedContent = "content";
        // when
        LocalDate newCreatedDate = LocalDate.ofEpochDay(diaryService.createOrModify(member.getId(),createdDate, new DiaryRequest(expectedTitle, expectedContent)));
        Diary diary = diaryRepository.findByDate(newCreatedDate).get();
        // then
        assertThat(diary.getTitle()).isEqualTo(expectedTitle);
        assertThat(diary.getContent()).isEqualTo(expectedContent);
    }


    @Test
    void 일기_수정한다() {
        // given
        Member member = createMember();
        LocalDate createdDate = LocalDate.of(2022, 1, 1);
        LocalDate newCreatedDate = LocalDate.ofEpochDay(diaryService.createOrModify(member.getId(), createdDate, new DiaryRequest("title", "content")));
        String expectedTitle = "title2";
        String expectedContent = "content2";
        // when
        diaryService.createOrModify(member.getId(), createdDate,new DiaryRequest(expectedTitle, expectedContent));
        Diary found = diaryRepository.findByDate(createdDate).get();
        // then
        assertThat(found.getTitle()).isEqualTo(expectedTitle);
        assertThat(found.getContent()).isEqualTo(expectedContent);
    }




    Member createMember() {
        Member member = Member.builder().build();
        em.persist(member);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.ofNullable(member));
        return member;
    }


}
