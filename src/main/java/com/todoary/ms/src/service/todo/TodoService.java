package com.todoary.ms.src.service.todo;

import com.todoary.ms.src.common.exception.TodoaryException;
import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Todo;
import com.todoary.ms.src.repository.TodoRepository;
import com.todoary.ms.src.service.CategoryService;
import com.todoary.ms.src.service.MemberService;
import com.todoary.ms.src.web.dto.common.PageResponse;
import com.todoary.ms.src.web.dto.todo.TodoAlarmRequest;
import com.todoary.ms.src.web.dto.todo.TodoRequest;
import com.todoary.ms.src.web.dto.todo.TodoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import static com.todoary.ms.src.common.response.BaseResponseStatus.USERS_CATEGORY_NOT_EXISTS;

@RequiredArgsConstructor
@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final MemberService memberService;
    private final CategoryService categoryService;
    private final TodoByCategoryCondition todoByCategoryCondition;

    @Transactional
    public Long saveTodo(Long memberId, TodoRequest request) {
        Member member = memberService.findById(memberId);
        Category category = categoryService.findCategoryByIdAndMember(request.getCategoryId(), member);
        return todoRepository.save(
                request.toEntity(member, category)
        ).getId();
    }

    @Transactional
    public TodoResponse updateTodo(Long memberId, Long todoId, TodoRequest request) {
        Member member = memberService.findById(memberId);
        Category category = categoryService.findCategoryByIdAndMember(request.getCategoryId(), member);
        Todo todo = findTodoByIdAndMember(todoId, member);
        todo.update(
                request.getTitle(),
                category,
                request.getIsAlarmEnabled(),
                request.getTargetDate(),
                request.getTargetTime()
        );
        return TodoResponse.from(todo);
    }

    @Transactional
    public void deleteTodo(Long memberId, Long todoId) {
        Member member = memberService.findById(memberId);
        Todo todo = findTodoByIdAndMember(todoId, member);
        todo.removeAssociations();
        todoRepository.delete(todo);
    }

    @Transactional
    public void markTodoAsDone(Long memberId, Long todoId, boolean isChecked) {
        Member member = memberService.findById(memberId);
        Todo todo = findTodoByIdAndMember(todoId, member);
        todo.check(isChecked);
    }

    @Transactional
    public void pinTodo(Long memberId, Long todoId, boolean isPinned) {
        Member member = memberService.findById(memberId);
        Todo todo = findTodoByIdAndMember(todoId, member);
        todo.pin(isPinned);
    }

    @Transactional
    public void updateTodoAlarm(Long memberId, Long todoId, TodoAlarmRequest request) {
        Member member = memberService.findById(memberId);
        Todo todo = findTodoByIdAndMember(todoId, member);
        todo.updateAlarm(
                request.getIsAlarmEnabled(),
                request.getTargetDate(),
                request.getTargetTime()
        );
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> findTodosByDate(Long memberId, LocalDate targetDate) {
        Member member = memberService.findById(memberId);
        return todoRepository.findByDateAndMember(targetDate, member)
                .stream().map(TodoResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> findTodosByCategory(Long memberId, Long categoryId) {
        Category category = categoryService.findCategoryByIdAndMember(categoryId, memberId);
        return todoRepository.findByCategoryAndSatisfy(category, todoByCategoryCondition.getPredicate())
                .stream().map(TodoResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Todo> findAllByDateTime(LocalDate targetDate, LocalTime targetTime) {
        List<Todo> todos = todoRepository.findAllByDate(targetDate);
        return todos.stream()
                .filter(todo -> todo.getTargetTime().equals(targetTime))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponse<TodoResponse> findTodoPageByCategory(Pageable pageable, Long memberId, Long categoryId) {
        Category category = categoryService.findCategoryByIdAndMember(categoryId, memberId);
        return PageResponse.of(
                todoRepository
                        .findSliceByCategoryAndSatisfy(pageable, category, todoByCategoryCondition.getPredicate())
                        .map(TodoResponse::from));
    }

    @Transactional(readOnly = true)
    public List<Integer> findDaysHavingTodoInMonth(Long memberId, YearMonth yearMonth) {
        Member member = memberService.findById(memberId);
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        return todoRepository.findBetweenDaysAndMember(firstDay, lastDay, member)
                .stream().map(todo -> todo.getTargetDate().getDayOfMonth())
                .distinct()
                .collect(Collectors.toList());
    }

    private Todo findTodoByIdAndMember(Long todoId, Member member) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoaryException(USERS_CATEGORY_NOT_EXISTS));
        if (!todo.getMember().equals(member))
            throw new TodoaryException(USERS_CATEGORY_NOT_EXISTS);
        return todo;
    }

}
