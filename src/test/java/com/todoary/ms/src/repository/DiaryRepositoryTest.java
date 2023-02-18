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



   
    Member createMember() {
        Member member = Member.builder().build();
        em.persist(member);
        return member;
    }
}
