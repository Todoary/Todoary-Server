package com.todoary.ms.src.service;

import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Color;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.common.exception.TodoaryException;
import com.todoary.ms.src.repository.CategoryRepository;
import com.todoary.ms.src.web.dto.category.CategoryRequest;
import com.todoary.ms.src.web.dto.category.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.todoary.ms.src.common.response.BaseResponseStatus.DUPLICATE_CATEGORY;
import static com.todoary.ms.src.common.response.BaseResponseStatus.USERS_CATEGORY_NOT_EXISTS;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MemberService memberService;

    @Transactional
    public Long saveCategory(Long memberId, CategoryRequest request) {
        Member member = memberService.findActiveMemberById(memberId);
        validateMembersCategoryTitle(member, request.getTitle());
        return categoryRepository.save(request.toEntity(member)).getId();
    }

    @Transactional
    public void updateCategory(Long memberId, Long categoryId, CategoryRequest request) {
        Member member = memberService.findActiveMemberById(memberId);
        Category category = findCategoryByIdAndMember(categoryId, member);
        if (!category.hasTitle(request.getTitle())) {
            validateMembersCategoryTitle(member, request.getTitle());
        }
        category.update(request.getTitle(), Color.from(request.getColor()));
    }

    private void validateMembersCategoryTitle(Member member, String title) {
        if (member.hasCategoryNamed(title))
            throw new TodoaryException(DUPLICATE_CATEGORY);
    }

    @Transactional
    public void deleteCategory(Long memberId, Long categoryId) {
        Category category = findCategoryByIdAndMember(categoryId, memberId);
        category.removeAssociations();
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponse[] findCategories(Long memberId) {
        Member member = memberService.findActiveMemberById(memberId);
        return member.getCategories()
                .stream().map(c -> new CategoryResponse(
                        c.getId(), c.getTitle(), c.getColor().getCode())
                ).toArray(CategoryResponse[]::new);
    }

    @Transactional(readOnly = true)
    public Category findCategoryByIdAndMember(Long categoryId, Long memberId) {
        Member member = memberService.findActiveMemberById(memberId);
        return findCategoryByIdAndMember(categoryId, member);
    }

    @Transactional(readOnly = true)
    public Category findCategoryByIdAndMember(Long categoryId, Member member) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new TodoaryException(USERS_CATEGORY_NOT_EXISTS));
        if (!category.has(member))
            throw new TodoaryException(USERS_CATEGORY_NOT_EXISTS);
        return category;
    }

}
