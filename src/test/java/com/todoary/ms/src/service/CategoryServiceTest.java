package com.todoary.ms.src.service;

import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.common.exception.TodoaryException;
import com.todoary.ms.src.repository.CategoryRepository;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.web.dto.category.CategoryResponse;
import com.todoary.ms.src.web.dto.category.CategoryRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.todoary.ms.src.common.response.BaseResponseStatus.DUPLICATE_CATEGORY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class CategoryServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    JpaCategoryService categoryService;

    @MockBean
    MemberRepository memberRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void Category_생성한다() {
        // given
        Member member = createMember();
        String expectedTitle = "title";
        Integer expectedColor = 10;
        // when
        Long categoryId = categoryService.saveCategory(member.getId(), new CategoryRequest(expectedTitle, expectedColor));
        // then
        Category found = categoryRepository.findById(categoryId).get();
        assertThat(found.getTitle()).isEqualTo(expectedTitle);
        assertThat(found.getColor().getCode()).isEqualTo(expectedColor);
    }

    @Test
    void 똑같은_title로_생성_불가() {
        // given
        Member member = createMember();
        String title = "title";
        Integer color = 10;
        categoryService.saveCategory(member.getId(), new CategoryRequest(title, color));

        // 아래 코드는 junit의 assertThrows를 이용하면 아래와 같다.
        // TodoaryException exception = Assertions.assertThrows(TodoaryException.class, () -> {
        // categoryService.saveCategory(member.getId(), new CategorySaveRequest(title, 20));
        // });

        // then
        assertThatThrownBy(() -> {
            // when
            categoryService.saveCategory(member.getId(), new CategoryRequest(title, 20));
        })
                .isInstanceOf(TodoaryException.class)
                .matches(e -> ((TodoaryException) e).getStatus().equals(DUPLICATE_CATEGORY));
    }

    @Test
    void Category_수정한다() {
        // given
        Member member = createMember();
        Long categoryId = categoryService.saveCategory(member.getId(), new CategoryRequest("title", 10));
        String expectedTitle = "title2";
        Integer expectedColor = 17;
        // when
        categoryService.updateCategory(member.getId(), categoryId, new CategoryRequest(expectedTitle, expectedColor));
        Category found = categoryRepository.findById(categoryId).get();
        // then
        assertThat(found.getTitle()).isEqualTo(expectedTitle);
        assertThat(found.getColor().getCode()).isEqualTo(expectedColor);
    }

    @Test
    void 다른_카테고리와_똑같은_title로_수정_불가() {
        // given
        Member member = createMember();
        String title = "title";
        Integer color = 10;
        Long categoryId = categoryService.saveCategory(member.getId(), new CategoryRequest(title, color));
        Long otherCategoryId = categoryService.saveCategory(member.getId(), new CategoryRequest("title12341235", 30));
        // when
        // 같은 카테고리를 똑같은 이름으로 수정하는 것은 괜찮음
        categoryService.updateCategory(member.getId(), categoryId, new CategoryRequest(title, 15));
        // then
        assertThatThrownBy(() -> {
            // 다른 카테고리와 똑같은 이름으로 수정할 수는 없다.
            categoryService.updateCategory(member.getId(), otherCategoryId, new CategoryRequest(title, 15));
        })
                .isInstanceOf(TodoaryException.class)
                .matches(e -> ((TodoaryException) e).getStatus().equals(DUPLICATE_CATEGORY));
    }

    @Test
    void Category_삭제한다() {
        // given
        Member member = createMember();
        Long categoryId = categoryService.saveCategory(member.getId(), new CategoryRequest("title", 10));
        // when
        categoryService.deleteCategory(member.getId(), categoryId);
        // then
        assertThat(categoryRepository.findById(categoryId)).isEmpty();
        assertThat(member.getCategories()).hasSize(0);
    }

    @Test
    void 멤버의_모든_카테고리_조회() {
        // given
        Member member = createMember();
        List<CategoryRequest> requests = List.of(
                new CategoryRequest("title1", 10),
                new CategoryRequest("title2", 15),
                new CategoryRequest("title3", 10)
        );
        for (CategoryRequest request : requests)
            categoryService.saveCategory(member.getId(), request);
        // when
        CategoryResponse[] categories = categoryService.findCategories(member.getId());
        // then
        assertThat(categories.length).isEqualTo(requests.size());
        for (int i = 0; i < categories.length; i++) {
            assertThat(categories[i].getTitle()).isEqualTo(requests.get(i).getTitle());
            assertThat(categories[i].getColor()).isEqualTo(requests.get(i).getColor());
        }
    }

    Member createMember() {
        Member member = Member.builder().build();
        em.persist(member);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.ofNullable(member));
        return member;
    }
}