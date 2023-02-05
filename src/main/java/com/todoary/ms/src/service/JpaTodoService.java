package com.todoary.ms.src.service;

import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Todo;
import com.todoary.ms.src.exception.common.TodoaryException;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.repository.TodoRepository;
import com.todoary.ms.src.web.dto.TodoRequest;
import com.todoary.ms.src.web.dto.TodoResponse;
import com.todoary.ms.src.web.dto.TodoUpdateAlarmRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import static com.todoary.ms.util.BaseResponseStatus.USERS_CATEGORY_NOT_EXISTS;
import static com.todoary.ms.util.BaseResponseStatus.USERS_EMPTY_USER_ID;

@Service
public class JpaTodoService {
    private final TodoRepository todoRepository;

    private final MemberRepository memberRepository;
    private final JpaCategoryService categoryService;

    @Autowired
    public JpaTodoService(TodoRepository todoRepository, MemberRepository memberRepository, JpaCategoryService categoryService) {
        this.todoRepository = todoRepository;
        this.memberRepository = memberRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public Long saveTodo(Long memberId, TodoRequest request) {
        Member member = findMemberById(memberId);
        Category category = findCategoryByIdAndMember(request.getCategoryId(), member);
        return todoRepository.save(
                request.toEntity(member, category)
        ).getId();
    }

    @Transactional
    public void updateTodo(Long memberId, Long todoId, TodoRequest request) {
        Member member = findMemberById(memberId);
        Category category = findCategoryByIdAndMember(request.getCategoryId(), member);
        Todo todo = findTodoByIdAndMember(todoId, member);
        todo.update(
                request.getTitle(),
                category,
                request.isAlarmEnabled(),
                request.getTargetDate(),
                request.getTargetTime()
        );
    }

    @Transactional
    public void deleteTodo(Long memberId, Long todoId) {
        Member member = findMemberById(memberId);
        Todo todo = findTodoByIdAndMember(todoId, member);
        todo.removeAssociations();
        todoRepository.delete(todo);
    }

    @Transactional
    public void markTodoAsDone(Long memberId, Long todoId, boolean isChecked) {
        Member member = findMemberById(memberId);
        Todo todo = findTodoByIdAndMember(todoId, member);
        todo.check(isChecked);
    }

    @Transactional
    public void pinTodo(Long memberId, Long todoId, boolean isPinned) {
        Member member = findMemberById(memberId);
        Todo todo = findTodoByIdAndMember(todoId, member);
        todo.pin(isPinned);
    }

    @Transactional
    public void updateTodoAlarm(Long memberId, Long todoId, TodoUpdateAlarmRequest request) {
        Member member = findMemberById(memberId);
        Todo todo = findTodoByIdAndMember(todoId, member);
        todo.updateAlarm(
                request.isAlarmEnabled(),
                request.getTargetDate(),
                request.getTargetTime()
        );
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> findTodosByDate(Long memberId, LocalDate targetDate) {
        Member member = findMemberById(memberId);
        return todoRepository.findByDateAndMember(targetDate, member)
                .stream().map(TodoResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> findTodosByCategoryStartingToday(Long memberId, Long categoryId) {
        Member member = findMemberById(memberId);
        Category category = findCategoryByIdAndMember(categoryId, member);
        return todoRepository.findByCategoryStartingToday(category)
                .stream().map(TodoResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public int[] findDaysHavingTodoInMonth(Long memberId, String yearAndMonth) {
        Member member = findMemberById(memberId);

        StringTokenizer st = new StringTokenizer(yearAndMonth, "-");
        LocalDate firstDay = LocalDate.of(
                Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        return todoRepository.findBetweenDaysAndMember(firstDay, lastDay, member)
                .stream().map(todo -> todo.getTargetDate().getDayOfMonth())
                .collect(Collectors.toSet())
                .stream().mapToInt(Number::intValue).toArray();
    }

    public static LocalDate convertToLocalDate(String date) {
        StringTokenizer st = new StringTokenizer(date, "-");
        int year = Integer.parseInt(st.nextToken());
        int month = Integer.parseInt(st.nextToken());
        int dayOfMonth = Integer.parseInt(st.nextToken());
        return LocalDate.of(year, month, dayOfMonth);
    }

    public static LocalTime convertToLocalTime(String time) {
        StringTokenizer st = new StringTokenizer(time, ":");
        int hour = Integer.parseInt(st.nextToken());
        int minute = Integer.parseInt(st.nextToken());
        return LocalTime.of(hour, minute);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new TodoaryException(USERS_EMPTY_USER_ID));
    }

    private Category findCategoryByIdAndMember(Long categoryId, Member member) {
        return categoryService.findCategoryByIdAndMember(categoryId, member);
    }

    private Todo findTodoByIdAndMember(Long todoId, Member member) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoaryException(USERS_CATEGORY_NOT_EXISTS));
        if (!todo.getMember().equals(member))
            throw new TodoaryException(USERS_CATEGORY_NOT_EXISTS);
        return todo;
    }

}
