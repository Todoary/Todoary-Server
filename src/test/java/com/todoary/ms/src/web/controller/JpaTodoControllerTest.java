package com.todoary.ms.src.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoary.ms.src.config.auth.WithTodoaryMockUser;
import com.todoary.ms.src.service.JpaTodoService;
import com.todoary.ms.src.todo.dto.PostTodoRes;
import com.todoary.ms.src.web.controller.JpaTodoController.MarkTodoRequest;
import com.todoary.ms.src.web.controller.JpaTodoController.PinTodoRequest;
import com.todoary.ms.src.web.dto.TodoRequest;
import com.todoary.ms.src.web.dto.TodoResponse;
import com.todoary.ms.util.BaseResponseStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import static com.todoary.ms.src.web.controller.JpaTodoControllerTest.REQUEST_URL.*;
import static com.todoary.ms.src.web.controller.TestUtils.getResponseObject;
import static com.todoary.ms.src.web.controller.TestUtils.getResponseObjectList;
import static com.todoary.ms.util.BaseResponseStatus.*;
import static com.todoary.ms.util.ColumnLengthInfo.TODO_TITLE_MAX_LENGTH;
import static com.todoary.ms.util.ColumnLengthInfo.getGraphemeLength;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JpaTodoController.class)
@Import(HttpEncodingAutoConfiguration.class)
class JpaTodoControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    private JpaTodoService todoService;

    // ObjectMapper를 주입받지 않고 직접 사용하면(new ObjectMapper())
    // LocalDateTime 계열을 직렬화/역직렬화할 수가 없어 에러 발생
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithTodoaryMockUser
    void 투두_제목_최대_이하일때_생성O() throws Exception {
        // given
        Long expected = 1L;
        given(todoService.saveTodo(any(), any())).willReturn(expected);

        String title = "제목";
        TodoRequest requestDto = TodoRequest.builder()
                .title(title)
                .categoryId(10L)
                .targetDate(LocalDate.of(2022, 2, 4))
                .targetTime(LocalTime.of(21, 40))
                .build();
        // when
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        PostTodoRes response = getResponseObject(result, PostTodoRes.class);
        // then
        assertThat(response.getTodoId()).isEqualTo(expected);
    }

    @Test
    @WithTodoaryMockUser
    void 투두_제목_최대_초과일때_생성X() throws Exception {
        // given
        String title = "오";
        // 최대 길이까지 반복
        int repeatCount = (int) Math.ceil((double) TODO_TITLE_MAX_LENGTH / getGraphemeLength(title)) + 1;
        title = title.repeat(repeatCount);
        TodoRequest requestDto = TodoRequest.builder()
                .title(title)
                .categoryId(10L)
                .targetDate(LocalDate.of(2022, 2, 4))
                .build();
        // when
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result);
        // then
        assertThat(status).isEqualTo(TODO_TITLE_TOO_LONG);
    }

    @Test
    @WithTodoaryMockUser
    void 투두_카테고리_null일때_생성X() throws Exception {
        // given
        Long categoryId = null;
        TodoRequest requestDto = TodoRequest.builder()
                .title("제목")
                .categoryId(categoryId)
                .targetDate(LocalDate.of(2022, 2, 4))
                .build();
        // when
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result);
        // then
        assertThat(status).isEqualTo(USERS_CATEGORY_NOT_EXISTS);
    }

    @Test
    @WithTodoaryMockUser
    void 투두_날짜가_null일때_생성X() throws Exception {
        // given
        TodoRequest requestDto = TodoRequest.builder()
                .title("제목")
                .categoryId(10L)
                .build();
        // when
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result);
        // then
        assertThat(status).isEqualTo(EMPTY_TODO_DATE);
    }

    @Test
    @WithTodoaryMockUser
    void 투두_날짜_형식이_틀릴때_생성X() throws Exception {
        // given
        TodoRequest requestDto = TodoRequest.builder()
                .title("제목")
                .categoryId(10L)
                .build();
        String json = objectMapper.writeValueAsString(requestDto).replace("\"targetDate\":null", "\"targetDate\":\"1234\"");
        // when
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(json))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result);
        // then
        assertThat(status).isEqualTo(ILLEGAL_ARGUMENT);
    }

    @Test
    @WithTodoaryMockUser
    void 투두_제목_최대_이하일때_수정O() throws Exception {
        // given
        Long expected = 1L;
        given(todoService.saveTodo(any(), any())).willReturn(expected);

        String title = "제목";
        TodoRequest requestDto = TodoRequest.builder()
                .title(title)
                .categoryId(10L)
                .targetDate(LocalDate.of(2022, 2, 4))
                .build();
        // when
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        PostTodoRes response = getResponseObject(result, PostTodoRes.class);
        // then
        assertThat(response.getTodoId()).isEqualTo(expected);
    }

    @Test
    @WithTodoaryMockUser
    void 날짜_형식_맞을때_날짜별_투두_조회O() throws Exception {
        // given
        LocalDate targetDate = LocalDate.of(2023, 2, 7);
        List<TodoResponse> expected = List.of(
                TodoResponse.builder().title("todo1").targetDate(targetDate).build(),
                TodoResponse.builder().title("todo2").targetDate(targetDate).build()
        );
        given(todoService.findTodosByDate(any(), eq(targetDate))).willReturn(expected);
        // when
        MvcResult result = mvc.perform(get(RETRIEVE_DATE, targetDate))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        List<TodoResponse> response = getResponseObjectList(result, TodoResponse.class, objectMapper);
        // then
        System.out.println("response = " + response);
        assertThat(response).containsAll(expected);
    }

    @Test
    @WithTodoaryMockUser
    void 날짜_형식_틀릴때_날짜별_투두_조회X() throws Exception {
        // given
        String incorrectDate = "1234";
        // when
        MvcResult result = mvc.perform(get(RETRIEVE_DATE, incorrectDate))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result);
        // then
        assertThat(status).isEqualTo(ILLEGAL_DATE);
    }

    @Test
    @WithTodoaryMockUser
    void 투두_수정O() throws Exception {
        TodoRequest requestDto = TodoRequest.builder()
                .title("제목")
                .categoryId(10L)
                .targetDate(LocalDate.of(2022, 2, 4))
                .targetTime(LocalTime.of(21, 40))
                .build();
        // when
        MvcResult result = mvc.perform(patch(REQUEST_URL.MODIFY, 1L).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus response = getResponseObject(result);
        // then
        assertThat(response).isEqualTo(SUCCESS);
    }

    // 투두 생성과 똑같은 request dto 사용하므로 나머지는 생략
    @Test
    @WithTodoaryMockUser
    void 투두_카테고리_null일때_수정X() throws Exception {
        // given
        Long categoryId = null;
        TodoRequest requestDto = TodoRequest.builder()
                .title("제목")
                .categoryId(categoryId)
                .targetDate(LocalDate.of(2022, 2, 4))
                .build();
        // when
        MvcResult result = mvc.perform(patch(REQUEST_URL.MODIFY, 1L).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result);
        // then
        assertThat(status).isEqualTo(USERS_CATEGORY_NOT_EXISTS);
    }

    @Test
    @WithTodoaryMockUser
    void 투두_삭제O() throws Exception {
        // given

        // when
        MvcResult result = mvc.perform(delete(REQUEST_URL.DELETE, 1L).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result);
        // then
        assertThat(status).isEqualTo(SUCCESS);
    }

    @Test
    @WithTodoaryMockUser
    void 투두_카테고리별_조회O() throws Exception {
        // given
        Long categoryId = 5L;
        List<TodoResponse> expected = List.of(
                TodoResponse.builder().categoryId(categoryId).build(),
                TodoResponse.builder().categoryId(categoryId).build()
        );
        given(todoService.findTodosByCategoryStartingToday(any(), eq(categoryId))).willReturn(expected);
        // when
        MvcResult result = mvc.perform(get(RETRIEVE_CATEGORY, categoryId))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        List<TodoResponse> response = getResponseObjectList(result, TodoResponse.class, objectMapper);
        // then
        assertThat(response).containsAll(expected);
    }

    @Test
    @WithTodoaryMockUser
    void 투두_체크박스_체크O() throws Exception {
        // given
        MarkTodoRequest request = MarkTodoRequest.builder()
                .todoId(1L)
                .isChecked(true)
                .build();
        // when
        MvcResult result = mvc.perform(patch(MARK).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result);
        // then
        assertThat(status).isEqualTo(SUCCESS);
    }

    @Test
    @WithTodoaryMockUser
    void 투두_id_비어있을때_체크X() throws Exception {
        // given
        MarkTodoRequest request = MarkTodoRequest.builder()
                .todoId(null)
                .isChecked(true)
                .build();
        // when
        MvcResult result = mvc.perform(patch(MARK).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result);
        // then
        assertThat(status).isEqualTo(NULL_ARGUMENT);
    }

    @Test
    @WithTodoaryMockUser
    void 투두_체크여부_비어있을때_체크X() throws Exception {
        // given
        MarkTodoRequest request = MarkTodoRequest.builder()
                .todoId(10L)
                .isChecked(null)
                .build();
        // when
        MvcResult result = mvc.perform(patch(MARK).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result);
        // then
        assertThat(status).isEqualTo(NULL_ARGUMENT);
    }

    @Test
    @WithTodoaryMockUser
    void 투두_핀_고정O() throws Exception {
        // given
        PinTodoRequest request = PinTodoRequest.builder()
                .todoId(1L)
                .isPinned(true)
                .build();
        // when
        MvcResult result = mvc.perform(patch(PIN).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result);
        // then
        assertThat(status).isEqualTo(SUCCESS);
    }

    @Test
    @WithTodoaryMockUser
    void 투두_id_비어있을때_고정X() throws Exception {
        // given
        PinTodoRequest request = PinTodoRequest.builder()
                .todoId(null)
                .isPinned(true)
                .build();
        // when
        MvcResult result = mvc.perform(patch(PIN).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result);
        // then
        assertThat(status).isEqualTo(NULL_ARGUMENT);
    }

    @Test
    @WithTodoaryMockUser
    void 투두_고정여부_비어있을때_체크X() throws Exception {
        // given
        PinTodoRequest request = PinTodoRequest.builder()
                .todoId(10L)
                .isPinned(null)
                .build();
        // when
        MvcResult result = mvc.perform(patch(PIN).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result);
        // then
        assertThat(status).isEqualTo(NULL_ARGUMENT);
    }

    @Test
    @WithTodoaryMockUser
    void 월별_투두_존재_날짜_조회O() throws Exception {
        // given
        YearMonth yearMonth = YearMonth.of(2023, 1);
        List<Integer> expected = List.of(1, 10);
        given(todoService.findDaysHavingTodoInMonth(any(), eq(yearMonth))).willReturn(expected);
        // when
        MvcResult result = mvc.perform(get(RETRIEVE_MONTH_DAYS, yearMonth))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        List<Integer> response = getResponseObjectList(result, Integer.class, objectMapper);
        // then
        assertThat(response).containsAll(expected);
    }

    @Test
    @WithTodoaryMockUser
    void 날짜_형식_틀리면_월별_투두_조회X() throws Exception {
        // given
        String illegalYearMonth = "1234";
        // when
        MvcResult result = mvc.perform(get(RETRIEVE_MONTH_DAYS, illegalYearMonth))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result);
        // then
        assertThat(status).isEqualTo(ILLEGAL_DATE);
    }

    static class REQUEST_URL {
        private static final String BASE = "/v2/todo";
        public static final String SAVE = BASE;
        public static final String MODIFY = BASE + "/{todoId}";
        public static final String RETRIEVE_DATE = BASE + "/date/{date}";
        public static final String RETRIEVE_CATEGORY = BASE + "/category/{categoryId}";
        public static final String DELETE = BASE + "/{todoId}";
        public static final String MARK = BASE + "/check";
        public static final String PIN = BASE + "/pin";
        public static final String RETRIEVE_MONTH_DAYS = BASE + "/days/{yearMonth}";
    }
}