package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.domain.Color;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.Todo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
    void 특정_Category의_Todo_조회() {
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
        List<Todo> todos = todoRepository.findByCategory(category);
        // then
        assertThat(todos).hasSize(1);
        assertThat(todos.get(0)).isEqualTo(todo);
        assertThat(category.getTodos()).hasSize(1);
        assertThat(category.getTodos().get(0)).isEqualTo(todo);
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