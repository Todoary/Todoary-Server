package com.todoary.ms.src.service;

import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Color;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.exception.common.TodoaryException;
import com.todoary.ms.src.repository.CategoryRepository;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.web.dto.CategoryResponse;
import com.todoary.ms.src.web.dto.CategorySaveRequest;
import com.todoary.ms.src.web.dto.CategoryUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.todoary.ms.util.BaseResponseStatus.*;

@Service
public class JpaCategoryService {

    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public JpaCategoryService(CategoryRepository categoryRepository, MemberRepository memberRepository) {
        this.categoryRepository = categoryRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Long saveCategory(Long memberId, CategorySaveRequest request) {
        Member member = findMemberById(memberId);
        validateMembersCategoryTitle(member, request.getTitle());
        return categoryRepository.save(request.toEntity(member)).getId();
    }

    @Transactional
    public void updateCategory(Long memberId, Long categoryId, CategoryUpdateRequest request) {
        Member member = findMemberById(memberId);
        Category target = findCategoryByIdAndMember(categoryId, member);
        Color nextColor = new Color(request.getColor());
        String nextTitle = request.getTitle();
        if (target.getTitle().equals(nextTitle)) {
            target.update(nextColor);
            return;
        }
        validateMembersCategoryTitle(member, nextTitle);
        target.update(nextTitle, nextColor);
    }

    private void validateMembersCategoryTitle(Member member, String title) {
        if (member.hasCategoryNamed(title))
            throw new TodoaryException(DUPLICATE_CATEGORY);
    }

    @Transactional
    public void deleteCategory(Long memberId, Long categoryId) {
        Member member = findMemberById(memberId);
        Category category = findCategoryByIdAndMember(categoryId, member);
        category.removeAssociations();
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponse[] findCategories(Long memberId) {
        Member member = findMemberById(memberId);
        return member.getCategories()
                .stream().map(c -> new CategoryResponse(
                        c.getId(), c.getTitle(), c.getColor().getCode())
                ).toArray(CategoryResponse[]::new);
    }

    @Transactional(readOnly = true)
    public Category findCategoryByIdAndMember(Long categoryId, Member member) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new TodoaryException(USERS_CATEGORY_NOT_EXISTS));
        if (!category.has(member))
            throw new TodoaryException(USERS_CATEGORY_NOT_EXISTS);
        return category;
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new TodoaryException(USERS_EMPTY_USER_ID));
    }

}
