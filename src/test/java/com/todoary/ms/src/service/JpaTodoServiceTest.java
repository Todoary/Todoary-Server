package com.todoary.ms.src.service;

import com.todoary.ms.src.domain.*;
import com.todoary.ms.src.repository.CategoryRepository;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Transactional @SpringBootTest class JpaTodoServiceTest {
    @Autowired
    EntityManager em;

    @Autowired
    JpaTodoService todoService;

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
        String date = "2022-01-04";
        String time = "22:17";
        TodoSaveRequest request = new TodoSaveRequest("todo", true, date, time, category.getId());
        // when
        Long todoId = todoService.saveTodo(member.getId(), request);
        Todo todo = todoRepository.findById(todoId).get();
        // then
        assertThat(member.getTodos()).hasSize(1);
        assertThat(category.getTodos()).hasSize(1);
        assertThat(category.getTodos().get(0)).isEqualTo(todo);
        assertThat(todo.getTargetDate().toString()).isEqualTo(date);
        assertThat(todo.getTargetTime().toString()).isEqualTo(time);
    }

    @Test
    void Todo_수정() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        TodoSaveRequest request = new TodoSaveRequest("todo", true, "2022-01-02", "15:10", category.getId());
        Long todoId = todoService.saveTodo(member.getId(), request);
        // when
        Category expectedCategory = createCategoryWithTitle(member, "category2");
        String title = "todo~!";
        String date = "2022-01-04";
        String time = "22:17";
        todoService.updateTodo(member.getId(), todoId, new TodoUpdateRequest(title, false, date, time, expectedCategory.getId()));
        Todo todo = todoRepository.findById(todoId).get();
        // then
        assertThat(todo.getTitle()).isEqualTo(title);
        assertThat(todo.getTargetDate().toString()).isEqualTo(date);
        assertThat(todo.getTargetTime().toString()).isEqualTo(time);
        assertThat(todo.getCategory()).isEqualTo(expectedCategory);
        assertThat(category.getTodos()).isEmpty();
        assertThat(expectedCategory.getTodos()).contains(todo);
    }

    @Test
    void Todo_알람_관련만_수정() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        TodoSaveRequest request = new TodoSaveRequest("todo", false, "2022-01-02", "15:10", category.getId());
        Long todoId = todoService.saveTodo(member.getId(), request);
        // when
        String date = "2022-01-04";
        String time = "22:17";
        boolean isAlarmEnabled = true;
        todoService.updateTodoAlarm(
                member.getId(), todoId, new TodoUpdateAlarmRequest(isAlarmEnabled, date, time));
        Todo todo = todoRepository.findById(todoId).get();
        // then
        assertThat(todo.getIsAlarmEnabled()).isEqualTo(isAlarmEnabled);
        assertThat(todo.getTargetDate().toString()).isEqualTo(date);
        assertThat(todo.getTargetTime().toString()).isEqualTo(time);
    }

    @Test
    void Todo_삭제() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        TodoSaveRequest request = new TodoSaveRequest("todo", true, "2022-01-02", "15:10", category.getId());
        Long todoId = todoService.saveTodo(member.getId(), request);
        // when
        todoService.deleteTodo(member.getId(), todoId);
        // then
        assertThat(todoRepository.findById(todoId)).isEmpty();
        assertThat(category.getTodos()).hasSize(0);
        assertThat(member.getTodos()).hasSize(0);
    }

    @Test
    void Todo_체크_표시한다() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        TodoSaveRequest request = new TodoSaveRequest("todo", true, "2022-01-02", "15:10", category.getId());
        Long todoId = todoService.saveTodo(member.getId(), request);
        // when
        todoService.markTodoAsDone(member.getId(), todoId, true);
        // then
        assertThat(todoRepository.findById(todoId).get().getIsChecked()).isTrue();
    }

    @Test
    void Todo_고정한다() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        TodoSaveRequest request = new TodoSaveRequest("todo", true, "2022-01-02", "15:10", category.getId());
        Long todoId = todoService.saveTodo(member.getId(), request);
        // when
        todoService.pinTodo(member.getId(), todoId, true);
        // then
        assertThat(todoRepository.findById(todoId).get().getIsPinned()).isTrue();
    }

    @Test
    void 특정_날짜의_Todo_조회() {
        // given
        Member member = createMember();
        Category category1 = createCategoryWithTitle(member, "category1");
        Category category2 = createCategoryWithTitle(member, "category2");
        String date = "2022-01-02";
        Long todoId1 = todoService.saveTodo(
                member.getId(),
                new TodoSaveRequest("todo1", true, date, "15:10", category1.getId())
        );
        Long todoId2 = todoService.saveTodo(
                member.getId(),
                new TodoSaveRequest("todo2", true, date, "12:20", category2.getId())
        );
        todoService.saveTodo(
                member.getId(),
                new TodoSaveRequest("todo3", true, "2022-01-05", "15:10", category1.getId())
        );
        // when
        TodoResponse[] todos = todoService.findTodosByDate(member.getId(), date);
        // then
        assertThat(todos).hasSize(2);
        assertThat(todos)
                .extracting(TodoResponse::getTodoId)
                .contains(todoId1, todoId2);
    }

    @Test
    void 특정_Category의_Todo_조회() {
        // given
        Member member = createMember();
        Category category1 = createCategoryWithTitle(member, "category1");
        Category category2 = createCategoryWithTitle(member, "category2");
        String date = "2022-01-02";
        todoService.saveTodo(
                member.getId(),
                new TodoSaveRequest("todo1", true, date, "15:10", category1.getId())
        );
        Long todoId = todoService.saveTodo(
                member.getId(),
                new TodoSaveRequest("todo2", true, date, "12:20", category2.getId())
        );
        todoService.saveTodo(
                member.getId(),
                new TodoSaveRequest("todo3", true, "2022-01-05", "15:10", category1.getId())
        );
        // when
        TodoResponse[] todos1 = todoService.findTodosByCategory(member.getId(), category1.getId());
        TodoResponse[] todos2 = todoService.findTodosByCategory(member.getId(), category2.getId());
        // then
        assertThat(todos1).hasSize(2);
        assertThat(todos2).hasSize(1);
        assertThat(todos2[0].getTodoId()).isEqualTo(todoId);
    }

    @Test
    void Todo_조회시_날짜_알람시간_생성시간_순으로_정렬() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        String[] dates = {"2022-01-10", "2022-01-05", "2022-01-05", "2022-01-05", "2022-01-12"};
        String[] times = {"10:00", "12:00", "09:45", "13:00", "14:00"};
        List<Long> todoIds = new ArrayList<>();
        for (int i = 0; i < dates.length; i++) {
            todoIds.add(todoService.saveTodo(
                    member.getId(),
                    new TodoSaveRequest("todo1", true, dates[i], times[i], category.getId())
            ));
        }
        // when
        TodoResponse[] todos = todoService.findTodosByCategory(member.getId(), category.getId());
        // then
        System.out.println("Arrays.toString(todos) = " + Arrays.toString(todos));
        assertThat(todos)
                .extracting(TodoResponse::getTodoId)
                .containsExactly(todoIds.get(2), todoIds.get(1), todoIds.get(3), todoIds.get(0), todoIds.get(4));
    }

    @Test
    void Todo_조회_시_생성시간_형식_확인() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        Pattern createdTimeFormat = Pattern.compile("^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01]) (0[0-9]|1[0-9]|2[0-3]):(0[1-9]|[0-5][0-9]):(0[1-9]|[0-5][0-9])$");
        String date = "2023-01-04";
        todoService.saveTodo(
                member.getId(),
                new TodoSaveRequest("todo1", true, date, "15:10", category.getId())
        );
        // when
        TodoResponse todo = todoService.findTodosByDate(member.getId(), date)[0];
        // then
        System.out.println("todo.getCreatedTime() = " + todo.getCreatedTime());
        assertThat(todo.getCreatedTime()).matches(createdTimeFormat);
    }

    @Test
    void Todo_조회_시_target_날짜_형식_확인() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        Pattern targetDateFormat = Pattern.compile("^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$");
        String date = "2023-01-04";
        todoService.saveTodo(
                member.getId(),
                new TodoSaveRequest("todo1", true, date, "15:10", category.getId())
        );
        // when
        TodoResponse todo = todoService.findTodosByDate(member.getId(), date)[0];
        // then
        assertThat(todo.getTargetDate()).matches(targetDateFormat);
    }

    @Test
    void Todo_조회_시_target_시간_형식_확인() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        Pattern targetTimeFormat = Pattern.compile("^(0[0-9]|1[0-9]|2[0-3]):(0[1-9]|[0-5][0-9])$");
        String date = "2023-01-01";
        String time = "22:22";
        todoService.saveTodo(
                member.getId(),
                new TodoSaveRequest("todo1", true, date, time, category.getId())
        );
        // when
        TodoResponse todo = todoService.findTodosByDate(member.getId(), date)[0];
        // then
        System.out.println("todo.getTargetTime() = " + todo.getTargetTime());
        assertThat(todo.getTargetTime()).matches(targetTimeFormat);
    }

    @Test
    void 특정_달의_Todo가_있는_날짜들_조회() {
        // given
        Member member = createMember();
        Category category = createCategoryWithTitle(member, "category");
        String[] dates = {"2023-01-01", "2023-01-20", "2023-01-20", "2023-01-30", "2023-02-10", "2023-03-25"};
        for (String date : dates) {
            todoService.saveTodo(
                    member.getId(),
                    new TodoSaveRequest("todo3", true, date, "10:10", category.getId())
            );
        }
        // when
        int[] january = todoService.findDaysHavingTodoInMonth(member.getId(), "2023-01");
        int[] february = todoService.findDaysHavingTodoInMonth(member.getId(), "2023-02");
        int[] march = todoService.findDaysHavingTodoInMonth(member.getId(), "2023-03");
        // then
        assertThat(january).hasSize(3);
        assertThat(february).hasSize(1);
        assertThat(march).hasSize(1);
    }

    Member createMember() {
        Member member = new Member();
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
}