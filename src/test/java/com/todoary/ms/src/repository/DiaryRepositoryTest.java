package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Transactional
@SpringBootTest
public class DiaryRepositoryTest {


    @Autowired
    EntityManager em;

    @Autowired
    DiaryRepository diaryRepository;

    /**
    @Test
    void Diary_저장_조회() {
        // given
        Member member = createMember();
        String expectedTitle = "title1";
        String expectedContent="content1";
        LocalDate createdDate=diaryRepository.save(new Diary(expectedTitle,expectedContent,member)).getCreatedDate();
        // when
        Diary diary = diaryRepository.findByDate(createdDate).get();
        // then
        assertThat(diary.getTitle()).isEqualTo(expectedTitle);
        assertThat(diary.getContent()).isEqualTo(expectedContent);
    }
    */

   
    Member createMember() {
        Member member = Member.builder().build();
        em.persist(member);
        return member;
    }
}
