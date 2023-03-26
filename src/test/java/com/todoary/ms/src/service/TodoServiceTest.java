package com.todoary.ms.src.service;

import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Color;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Todo;
import com.todoary.ms.src.repository.CategoryRepository;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.repository.TodoRepository;
import com.todoary.ms.src.service.todo.TodoService;
import com.todoary.ms.src.web.dto.common.PageResponse;
import com.todoary.ms.src.web.dto.todo.TodoAlarmRequest;
import com.todoary.ms.src.web.dto.todo.TodoRequest;
import com.todoary.ms.src.web.dto.todo.TodoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class TodoServiceTest {
    @Autowired
    EntityManager em;

    @Autowired
    TodoService todoService;

    @Autowired
    TodoRepository todoRepository;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    CategoryRepository categoryRepository;

    @Test
    void Todo_생성() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        LocalDate date = LocalDate.of(2022, 1, 4);
        LocalTime time = LocalTime.of(19, 48);
        TodoRequest request = TodoRequest.builder()
                .title("todo")
                .isAlarmEnabled(true)
                .targetDate(date)
                .targetTime(time)
                .categoryId(category.getId())
                .build();
        // when
        Long todoId = todoService.createMembersTodo(member.getId(), request);
        Todo todo = todoRepository.findById(todoId).get();
        // then
        assertThat(member.getTodos()).hasSize(1);
        assertThat(category.getTodos()).hasSize(1);
        assertThat(category.getTodos().get(0)).isEqualTo(todo);
        assertThat(todo.getTargetDate()).isEqualTo(date);
        assertThat(todo.getTargetTime()).isEqualTo(time);
    }

    @Test
    void Todo_수정() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        TodoRequest request = TodoRequest.builder()
                .title("todo")
                .isAlarmEnabled(true)
                .targetDate(LocalDate.of(2022, 1, 4))
                .targetTime(LocalTime.of(19, 40))
                .categoryId(category.getId())
                .build();
        Long todoId = todoService.createMembersTodo(member.getId(), request);
        // when
        Category expectedCategory = createCategoryWithTitle(member, "category2");
        String title = "todo~!";
        LocalDate expectedDate = LocalDate.of(2022, 1, 4);
        LocalTime expectedTime = LocalTime.of(19, 48);
        todoService.updateMembersTodo(member.getId(), todoId, new TodoRequest(title, false, expectedDate, expectedTime, expectedCategory.getId()));
        Todo todo = todoRepository.findById(todoId).get();
        // then
        assertThat(todo.getTitle()).isEqualTo(title);
        assertThat(todo.getTargetDate()).isEqualTo(expectedDate);
        assertThat(todo.getTargetTime()).isEqualTo(expectedTime);
        assertThat(todo.getCategory()).isEqualTo(expectedCategory);
        assertThat(todoService.retrieveMembersTodosByCategory(member.getId(), category.getId())).isEmpty();
        assertThat(expectedCategory.getTodos()).contains(todo);
    }

    @Test
    void Todo_알람_관련만_수정() {
        // given
        Member member = createMember();
        Long todoId = createDefaultTodo(member);
        // when
        LocalDate expectedDate = LocalDate.of(2022, 1, 4);
        LocalTime expectedTime = LocalTime.of(19, 48);
        boolean isAlarmEnabled = true;
        todoService.updateMembersTodoAlarm(
                member.getId(), todoId, TodoAlarmRequest.builder()
                        .isAlarmEnabled(isAlarmEnabled)
                        .targetDate(expectedDate)
                        .targetTime(expectedTime)
                        .build());
        Todo todo = todoRepository.findById(todoId).get();
        // then
        assertThat(todo.getIsAlarmEnabled()).isEqualTo(isAlarmEnabled);
        assertThat(todo.getTargetDate()).isEqualTo(expectedDate);
        assertThat(todo.getTargetTime()).isEqualTo(expectedTime);
    }

    @Test
    void Todo_삭제_카테고리와_멤버_연관관계도_삭제됨() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        TodoRequest request = TodoRequest.builder()
                .title("todo")
                .isAlarmEnabled(true)
                .targetDate(LocalDate.of(2022, 1, 4))
                .targetTime(LocalTime.of(19, 40))
                .categoryId(category.getId())
                .build();
        Long todoId = todoService.createMembersTodo(member.getId(), request);
        // when
        todoService.deleteMembersTodo(member.getId(), todoId);
        // then
        assertThat(todoRepository.findById(todoId)).isEmpty();
        assertThat(category.getTodos()).hasSize(0);
        assertThat(member.getTodos()).hasSize(0);
    }

    @Test
    void Category_삭제_시_Todo_삭제() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        TodoRequest request = TodoRequest.builder()
                .title("todo")
                .isAlarmEnabled(true)
                .targetDate(LocalDate.of(2022, 1, 4))
                .targetTime(LocalTime.of(19, 40))
                .categoryId(category.getId())
                .build();
        Long todoId = todoService.createMembersTodo(member.getId(), request);
        // when
        em.remove(category);
        // then
        assertThat(todoRepository.findById(todoId)).isEmpty();
    }

    @Test
    void Todo_체크_표시한다() {
        // given
        Member member = createMember();
        Long todoId = createDefaultTodo(member);
        // when
        todoService.updateMembersTodoMarkedStatus(member.getId(), todoId, true);
        // then
        assertThat(todoRepository.findById(todoId).get().getIsChecked()).isTrue();
    }

    @Test
    void Todo_고정한다() {
        // given
        Member member = createMember();
        Long todoId = createDefaultTodo(member);
        // when
        todoService.updateMembersTodoPinnedStatus(member.getId(), todoId, true);
        // then
        assertThat(todoRepository.findById(todoId).get().getIsPinned()).isTrue();
    }

    @Test
    void 특정_날짜의_Todo_조회() {
        // given
        Member member = createMember();
        Category category1 = createCategoryWithTitle(member, "category1");
        Category category2 = createCategoryWithTitle(member, "category2");
        LocalDate otherDate = LocalDate.of(2021, 11, 11);
        LocalDate date = LocalDate.of(2022, 1, 2);
        Long todoId1 = todoService.createMembersTodo(
                member.getId(),
                new TodoRequest("todo1", true, date, LocalTime.of(10, 10), category1.getId())
        );
        Long todoId2 = todoService.createMembersTodo(
                member.getId(),
                new TodoRequest("todo2", true, date, LocalTime.of(12, 20), category2.getId())
        );
        todoService.createMembersTodo(
                member.getId(),
                new TodoRequest("todo3", true, otherDate, LocalTime.of(15, 10), category1.getId())
        );
        // when
        List<TodoResponse> todos = todoService.retrieveMembersTodosOnDate(member.getId(), date);
        // then
        assertThat(todos).hasSize(2);
        assertThat(todos)
                .extracting(TodoResponse::getTodoId)
                .contains(todoId1, todoId2);
    }

    @Test
    void 특정_Category의_Todo_조회시_오늘부터만_조회() {
        // given
        Member member = createMember();
        Category category1 = createCategoryWithTitle(member, "category1");
        Category category2 = createCategoryWithTitle(member, "category2");
        LocalDate now = LocalDate.now();
        LocalDate beforeNow = now.minusDays(1);
        IntStream.range(0, 5)
                .forEach(__ -> todoService.createMembersTodo(
                        member.getId(),
                        TodoRequest.builder()
                                .targetDate(beforeNow)
                                .categoryId(category1.getId())
                                .build()
                ));
        IntStream.range(0, 3)
                .forEach(__ -> todoService.createMembersTodo(
                        member.getId(),
                        TodoRequest.builder()
                                .targetDate(now)
                                .categoryId(category1.getId())
                                .build()
                ));
        // when
        List<TodoResponse> todos1 = todoService.retrieveMembersTodosByCategory(member.getId(), category1.getId());
        List<TodoResponse> todos2 = todoService.retrieveMembersTodosByCategory(member.getId(), category2.getId());
        // then
        assertThat(todos1).hasSize(3);
        assertThat(todos2).isEmpty();
    }

    @Test
    void 카테고리별_Todo_조회시_날짜_알람시간_생성시간_순으로_정렬() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        LocalDate now = LocalDate.now();
        LocalDate[] dates = {
                now.plusDays(10),
                now.plusDays(5),
                now.plusDays(5),
                now.plusDays(5),
                now.plusDays(12)};
        LocalTime[] times = {
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                LocalTime.of(9, 45),
                // 시간 없는 건 가장 나중으로 정렬된다
                null,
                LocalTime.of(22, 15)};
        List<Long> todoIds = IntStream.range(0, 5)
                .mapToObj(i -> todoService.createMembersTodo(member.getId(), TodoRequest.builder()
                        .targetDate(dates[i])
                        .targetTime(times[i])
                        .categoryId(category.getId())
                        .build()))
                .collect(Collectors.toList());
        // when
        List<TodoResponse> todos = todoService.retrieveMembersTodosByCategory(member.getId(), category.getId());
        // then
        assertThat(todos)
                .extracting(TodoResponse::getTodoId)
                .containsExactly(todoIds.get(2), todoIds.get(1), todoIds.get(3), todoIds.get(0), todoIds.get(4));
    }

    @Test
    void 카테고리별_투두_조회시_페이징_적용() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        LocalDate now = LocalDate.now();
        // 5개 생성
        List<Long> todoIds = IntStream.range(0, 5)
                .mapToObj(i -> todoService.createMembersTodo(member.getId(), TodoRequest.builder()
                        .targetDate(now)
                        .targetTime(LocalTime.of(14, 10))
                        .categoryId(category.getId())
                        .build()))
                .collect(Collectors.toList());
        Pageable pageable = PageRequest.of(0, 2);
        // when
        // 첫번째 페이지
        PageResponse<TodoResponse> firstPage = todoService.findTodoPageByCategory(pageable, member.getId(), category.getId());
        PageResponse<TodoResponse> secondPage = todoService.findTodoPageByCategory(pageable.next(), member.getId(), category.getId());
        PageResponse<TodoResponse> lastPage = todoService.findTodoPageByCategory(pageable.next().next(), member.getId(), category.getId());
        PageResponse<TodoResponse> emptyPage = todoService.findTodoPageByCategory(pageable.next().next().next(), member.getId(), category.getId());
        List<PageResponse<TodoResponse>> pages = List.of(firstPage, secondPage, lastPage, emptyPage);
        // then
        List<Integer> numsOfElements = List.of(2, 2, 1, 0);
        List<Boolean> empty = List.of(false, false, false, true);
        List<Boolean> last = List.of(false, false, true, true);

        assertThat(pages).map(page -> page.getContents().size()).containsExactlyElementsOf(numsOfElements);
        assertThat(pages).map(page -> page.getPageInfo().isEmpty()).containsExactlyElementsOf(empty);
        assertThat(pages).map(page -> page.getPageInfo().isLast()).containsExactlyElementsOf(last);
        // 날짜와 시간이 같은 투두를 생성했으므로 생성 순서대로 정렬되어 조회됨
        assertThat(pages)
                .flatMap(page -> page.getContents().stream()
                        .map(TodoResponse::getTodoId).collect(Collectors.toList()))
                .containsExactlyElementsOf(todoIds);
    }

    @Test
    void 특정_달의_Todo가_있는_날짜들_조회() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        LocalDate[] dates = {
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 1, 20),
                LocalDate.of(2023, 1, 20),
                LocalDate.of(2023, 1, 30),
                LocalDate.of(2023, 2, 10),
                LocalDate.of(2023, 3, 25)};
        Arrays.stream(dates)
                .forEach(date -> todoService.createMembersTodo(
                        member.getId(),
                        new TodoRequest("todo", true, date, LocalTime.of(10, 8), category.getId())
                ));
        // when
        List<Integer> january = todoService.retrieveDaysHavingTodoOfMemberInMonth(member.getId(), YearMonth.of(2023, 1));
        List<Integer> february = todoService.retrieveDaysHavingTodoOfMemberInMonth(member.getId(), YearMonth.of(2023, 2));
        List<Integer> march = todoService.retrieveDaysHavingTodoOfMemberInMonth(member.getId(), YearMonth.of(2023, 3));
        // then
        assertThat(january).hasSize(3);
        assertThat(february).hasSize(1);
        assertThat(march).hasSize(1);
    }

    Member createMember() {
        Member member = Member.builder().email("email").build();
        em.persist(member);
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        return member;
    }

    Category createCategoryWithTitle(Member member, String title) {
        Category category = new Category(title, new Color(10), member);
        em.persist(category);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        return category;
    }

    private Long createDefaultTodo(Member member) {
        Category category = createCategoryWithTitle(member, "category");
        TodoRequest request = TodoRequest.builder()
                .title("todo")
                .isAlarmEnabled(true)
                .targetDate(LocalDate.of(2022, 1, 4))
                .targetTime(LocalTime.of(19, 40))
                .categoryId(category.getId())
                .build();
        return todoService.createMembersTodo(member.getId(), request);
    }
}