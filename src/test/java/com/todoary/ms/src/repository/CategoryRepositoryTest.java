package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Color;
import com.todoary.ms.src.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class CategoryRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void Category_저장_조회() {
        // given
        Member member = createMember();
        String title = "title";
        Color color = new Color(10);
        Long categoryId = categoryRepository.save(new Category(title, color, member)).getId();
        // when
        Category found = categoryRepository.findById(categoryId).get();
        // then
        assertThat(found.getTitle()).isEqualTo(title);
        assertThat(found.getColor()).isEqualTo(color);
    }

    @Test
    void Category_수정() {
        // given
        Member member = createMember();
        Category category = categoryRepository.save(new Category("title", new Color(10), member));
        String expectedTitle = "title2";
        Color expectedColor = new Color(17);
        category.update(expectedTitle, expectedColor);
        // when
        Category found = categoryRepository.findById(category.getId()).get();
        // then
        assertThat(found.getTitle()).isEqualTo(expectedTitle);
        assertThat(found.getColor()).isEqualTo(expectedColor);
    }

    @Test
    void Category_삭제() {
        // given
        Member member = createMember();
        Category category = categoryRepository.save(new Category("title", new Color(10), member));
        Long id = category.getId();
        // when
        categoryRepository.delete(category);
        // then
        assertThat(categoryRepository.findById(id)).isEmpty();
    }

    @Test
    void BaseTimeEntity로_생성_수정_시간_저장() {
        // given
        Member member = createMember();
        LocalDateTime now = LocalDateTime.now();
        Category category = categoryRepository.save(new Category("title", new Color(10), member));
        // when
        Category found = categoryRepository.findById(category.getId()).get();
        // then
        System.out.println("found.getCreatedAt() = " + found.getCreatedAt());
        System.out.println("found.getModifiedAt() = " + found.getModifiedAt());
        assertThat(found.getCreatedAt()).isAfter(now);
        assertThat(found.getModifiedAt()).isAfter(now);
    }

    Member createMember() {
        Member member = new Member();
        em.persist(member);
        return member;
    }
}