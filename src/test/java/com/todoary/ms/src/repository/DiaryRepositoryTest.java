package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.Diary;
import com.todoary.ms.src.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class DiaryRepositoryTest {


    @Autowired
    EntityManager em;

    @Autowired
    DiaryRepository diaryRepository;



    @Test
    void Diary_저장_조회() {
        // given
        Member member = createMember();
        String title = "title";
        String content = "content";
        LocalDate createdDate = LocalDate.ofEpochDay(diaryRepository.save(new Diary(title, content, member)).getId());
        // when
        Diary found = diaryRepository.findByDate(createdDate).get();
        // then
        assertThat(found.getTitle()).isEqualTo(title);
        assertThat(found.getContent()).isEqualTo(content);
    }

    @Test
    void Diary_수정() {
        // given
        Member member = createMember();
        Diary diary= diaryRepository.save(new Diary("title","content", member));
        String expectedTitle = "title2";
        String expectedContent = "content2";
        LocalDate expectedModifiedDate = LocalDate.of(2023, 1, 2);
        diary.update(expectedTitle, expectedContent, expectedModifiedDate);
        // when
        Diary found = diaryRepository.findByDate(expectedModifiedDate).get();
        // then
        assertThat(found.getTitle()).isEqualTo(expectedTitle);
        assertThat(found.getContent()).isEqualTo(expectedContent);
    }

    @Test
    void Diary_삭제() {
        // given
        Member member = createMember();
        Diary diary= diaryRepository.save(new Diary("title","content", member));
        LocalDate createdDate= diary.getCreatedDate();
        // when
        diary.removeAssociations();
        diaryRepository.delete(diary);
        // then
        assertThat(diaryRepository.findByDate(createdDate)).isEmpty();
        assertThat(member.getDiaries()).hasSize(0);
    }


    Member createMember() {
        Member member = Member.builder().build();
        em.persist(member);
        return member;
    }
}
