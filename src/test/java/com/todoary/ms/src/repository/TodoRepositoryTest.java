package com.todoary.ms.src.repository;

import com.querydsl.core.types.Predicate;
import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Color;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Todo;
import com.todoary.ms.src.service.todo.TodoStartingTodayCondition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class TodoRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    TodoRepository todoRepository;

    @Test
    void Todo_저장_조회() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        String title = "todo";
        LocalDate targetDate = LocalDate.of(2023, 2, 22);
        LocalTime targetTime = LocalTime.of(22, 17);
        Todo todo = todoRepository.save(Todo.builder()
                                                .title(title)
                                                .category(category)
                                                .member(member)
                                                .targetDate(targetDate)
                                                .targetTime(targetTime)
                                                .isAlarmEnabled(true)
                                                .build());
        // when
        Todo found = todoRepository.findById(todo.getId()).get();
        // then
        assertThat(found).isEqualTo(todo);
        assertThat(member.getTodos().get(0)).isEqualTo(found);
        /*
        found.getTargetDate() = 2023-02-22
        found.getTargetTime() = 22:17
         */
        System.out.println("found.getTargetDate() = " + found.getTargetDate());
        System.out.println("found.getTargetTime() = " + found.getTargetTime());
    }

    @Test
    void Todo_수정() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category1");
        LocalDate date = LocalDate.of(2022, 12, 25);
        LocalTime time = LocalTime.of(10, 2);
        Todo todo = todoRepository.save(Todo.builder()
                                                .title("todo1")
                                                .category(category)
                                                .member(member)
                                                .targetDate(date.plusDays(1))
                                                .targetTime(time)
                                                .isAlarmEnabled(true)
                                                .build());
        String title = "todo";
        Category expectedCategory = createCategoryWithTitle(member, "category2");

        boolean expectedEnabled = false;
        // when
        todo.update(title, expectedCategory, expectedEnabled, date, time);
        Todo found = todoRepository.findById(todo.getId()).get();
        // then
        assertThat(found).isEqualTo(todo);
    }

    @Test
    void Todo_삭제() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        Todo todo = todoRepository.save(Todo.builder()
                                                .title("title")
                                                .category(category)
                                                .member(member)
                                                .targetDate(LocalDate.of(2000, 11, 11))
                                                .targetTime(LocalTime.of(11, 11))
                                                .isAlarmEnabled(true)
                                                .build());
        Long todoId = todo.getId();
        // when
        todo.removeAssociations();
        todoRepository.delete(todo);
        // then
        assertThat(todoRepository.findById(todoId)).isEmpty();
        assertThat(member.getTodos()).hasSize(0);
        assertThat(category.getTodos()).hasSize(0);
    }

    @Test
    void 특정_날짜의_Todo_조회() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        String title = "todo";
        LocalDate targetDate = LocalDate.of(2023, 01, 04);
        LocalTime targetTime = LocalTime.of(12, 23);
        Todo todo = todoRepository.save(Todo.builder()
                                                .title(title)
                                                .category(category)
                                                .member(member)
                                                .targetDate(targetDate)
                                                .targetTime(targetTime)
                                                .isAlarmEnabled(true)
                                                .build());
        // 다른 날짜의 투두. 조회에 포함되면 안됨
        todoRepository.save(Todo.builder()
                                    .title("tododododo")
                                    .category(category)
                                    .member(member)
                                    .targetDate(targetDate.minusDays(1))
                                    .targetTime(targetTime)
                                    .isAlarmEnabled(true)
                                    .build());
        // when
        List<Todo> todos = todoRepository.findByDateAndMember(targetDate, member);
        // then
        assertThat(todos).hasSize(1);
        assertThat(todos.get(0)).isEqualTo(todo);
    }

    @Test
    void Category의_투두_조회시_오늘부터만_조회() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        String title = "todo";
        LocalDate now = LocalDate.now();
        System.out.println("now = " + now);
        LocalTime targetTime = LocalTime.of(22, 17);
        IntStream.range(-2, 3) // -2 <= < 3
                .mapToObj(day -> Todo.builder()
                        .title(title)
                        .category(category)
                        .member(member)
                        .targetDate(now.plusDays(day))
                        .targetTime(targetTime)
                        .isAlarmEnabled(true)
                        .build())
                .forEach(todo -> todoRepository.save(todo));
        Predicate condition = new TodoStartingTodayCondition().getPredicate();
        // when
        List<Todo> todos = todoRepository.findByCategoryAndSatisfy(category, condition);
        // then
        assertThat(todos).hasSize(3); // 2일전, 1일전, 오늘, 내일, 모레이므로 3개만 조회된다
        assertThat(todos)
                .map(Todo::getTargetDate)
                .allMatch(date -> date.isAfter(now) || date.isEqual(now));
    }

    @Test
    void 카테고리로_투두_조회_페이징_알맞은_범위() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        String title = "todo";
        LocalDate now = LocalDate.now();
        LocalTime targetTime = LocalTime.of(22, 17);
        IntStream.range(-2, 3) // -2 <= < 3
                .mapToObj(day -> Todo.builder()
                        .title(title)
                        .category(category)
                        .member(member)
                        .targetDate(now.plusDays(day))
                        .targetTime(targetTime)
                        .isAlarmEnabled(true)
                        .build())
                .forEach(todo -> todoRepository.save(todo));
        Predicate condition = new TodoStartingTodayCondition().getPredicate();
        int pageIndex = 0; // zero based page index
        int pageSize = 2;
        PageRequest firstPage = PageRequest.of(pageIndex, pageSize);
        PageRequest secondPage = PageRequest.of(pageIndex + 1, pageSize);
        // when
        Slice<Todo> todos = todoRepository.findSliceByCategoryAndSatisfy(firstPage, category, condition);
        Slice<Todo> rests = todoRepository.findSliceByCategoryAndSatisfy(secondPage, category, condition);
        // then
        int todoStartingToday = 3; // 2일전, 1일전, 오늘, 내일, 모레이므로 3개만 오늘부터 시작하는 투두
        int totalPages = (int) Math.ceil((double) todoStartingToday / pageSize);
        // 첫번째 페이지
        assertThat(todos.isFirst()).isTrue();
        assertThat(todos).hasSize(pageSize);
        assertThat(todos.getNumberOfElements()).isEqualTo(pageSize);
        assertThat(todos)
                .map(Todo::getTargetDate)
                .allMatch(date -> date.isAfter(now) || date.isEqual(now));
        // 2번째 페이지
        assertThat(rests.isLast()).isTrue();
        assertThat(rests).hasSize(todoStartingToday - pageSize); // 다음 페이지므로 첫번째 조회된 것 제외
        assertThat(rests.getNumberOfElements()).isEqualTo(todoStartingToday - pageSize);
        // 1번재 2번째 모두 오늘 날짜거나 이후인지 확인
        assertThat(Stream.of(todos.getContent(), rests.getContent()).flatMap(Collection::stream))
                .map(Todo::getTargetDate)
                .allMatch(date -> date.isAfter(now) || date.isEqual(now));
        // 정렬 순 맞는 지 확인
        Stream.of(todos.getContent(), rests.getContent()).flatMap(Collection::stream)
                .reduce((before, after) -> {
                    assertThat(after.getTargetDate()).isAfterOrEqualTo(before.getTargetDate());
                    if (after.getTargetDate().isEqual(before.getTargetDate())) {
                        assertThat(after.getTargetTime()).isAfterOrEqualTo(before.getTargetTime());
                    }
                    return after;
                });
    }

    @Test
    void 카테고리로_조회_시_없는_페이지_요청() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        Predicate condition = new TodoStartingTodayCondition().getPredicate();
        // when
        Slice<Todo> todos = todoRepository.findSliceByCategoryAndSatisfy(PageRequest.of(0, 5), category, condition);
        // then
        assertThat(todos).hasSize(0);
        assertThat(todos.isEmpty()).isTrue();
        assertThat(todos.isLast()).isTrue();
    }

    @Test
    void 특정_달의_Todo_조회() {
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        String title = "todo";
        int year = 2023;
        int month = 2;
        List<LocalDate> dates = List.of(
                LocalDate.of(year, month, 12),
                LocalDate.of(year, month, 12),
                LocalDate.of(year, month, 20),
                LocalDate.of(year, month + 5, 12)
        );
        LocalTime targetTime = LocalTime.of(22, 17);
        for (LocalDate date : dates) {
            todoRepository.save(Todo.builder()
                                        .title(title)
                                        .category(category)
                                        .member(member)
                                        .targetDate(date)
                                        .targetTime(targetTime)
                                        .isAlarmEnabled(true)
                                        .build());
        }
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
        // when
        List<Todo> found = todoRepository.findBetweenDaysAndMember(firstDay, lastDay, member);
        // then
        assertThat(found)
                .hasSize(3)
                .extracting(Todo::getTargetDate)
                .contains(dates.get(0), dates.get(1), dates.get(2));
    }

    Member createMember() {
        Member member = Member.builder()
                .build();
        em.persist(member);
        return member;
    }

    Category createCategoryWithTitle(Member member, String title) {
        Category category = new Category(title, new Color(10), member);
        em.persist(category);
        return category;
    }

}