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

import java.util.List;
import java.util.stream.Collectors;

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
        Member member = getMemberById(memberId);
        if (member.findCategoryNamed(request.getTitle()).isPresent())
            throw new TodoaryException(DUPLICATE_CATEGORY);
        return categoryRepository.save(request.toEntity(member)).getId();
    }

    @Transactional
    public void updateCategory(Long memberId, Long categoryId, CategoryUpdateRequest request) {
        Member member = getMemberById(memberId);
        Category target = findCategoryByIdAndMember(categoryId, member);
        Color nextColor = new Color(request.getColor());
        String nextTitle = request.getTitle();
        if (target.getTitle().equals(nextTitle)) {
            target.update(nextColor);
            return;
        }
        if (member.findCategoryNamed(nextTitle).isPresent())
            throw new TodoaryException(DUPLICATE_CATEGORY);
        target.update(nextTitle, nextColor);
    }

    @Transactional
    public void deleteCategory(Long memberId, Long categoryId) {
        Member member = getMemberById(memberId);
        Category category = findCategoryByIdAndMember(categoryId, member);
        category.removeAssociations();
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findCategories(Long memberId) {
        Member member = getMemberById(memberId);
        return member.getCategories()
                .stream().map(c -> new CategoryResponse(
                        c.getId(), c.getTitle(), c.getColor().getCode())
                ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Category findCategoryByIdAndMember(Long categoryId, Member member) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new TodoaryException(USERS_CATEGORY_NOT_EXISTS));
        if (!category.getMember().equals(member))
            throw new TodoaryException(USERS_CATEGORY_NOT_EXISTS);
        return category;
    }

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new TodoaryException(USERS_EMPTY_USER_ID));
    }

}
