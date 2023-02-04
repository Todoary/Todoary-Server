package com.todoary.ms.src.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoary.ms.src.config.auth.WithTodoaryMockUser;
import com.todoary.ms.src.service.JpaTodoService;
import com.todoary.ms.src.todo.dto.PostTodoRes;
import com.todoary.ms.src.web.dto.TodoRequest;
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

import static com.todoary.ms.util.BaseResponseStatus.*;
import static com.todoary.ms.util.ColumnLengthInfo.TODO_TITLE_MAX_LENGTH;
import static com.todoary.ms.util.ColumnLengthInfo.getGraphemeLength;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        given(todoService.saveTodo(any(),any())).willReturn(expected);

        String title = "제목";
        TodoRequest requestDto = TodoRequest.builder()
                .title(title)
                .categoryId(10L)
                .targetDate(LocalDate.of(2022,2,4))
                .targetTime(LocalTime.of(21, 40))
                .build();
        // when
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        PostTodoRes response = TestUtils.getResponseObject(result, PostTodoRes.class);
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
                .targetDate(LocalDate.of(2022,2,4))
                .build();
        // when
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = TestUtils.getResponseObject(result);
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
                .targetDate(LocalDate.of(2022,2,4))
                .build();
        // when
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = TestUtils.getResponseObject(result);
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
        BaseResponseStatus status = TestUtils.getResponseObject(result);
        // then
        assertThat(status).isEqualTo(EMPTY_TODO_DATE);
    }
    @Test
    @WithTodoaryMockUser
    void 투두_제목_최대_이하일때_수정O() throws Exception {
        // given
        Long expected = 1L;
        given(todoService.saveTodo(any(),any())).willReturn(expected);

        String title = "제목";
        TodoRequest requestDto = TodoRequest.builder()
                .title(title)
                .categoryId(10L)
                .targetDate(LocalDate.of(2022,2,4))
                .build();
        // when
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        PostTodoRes response = TestUtils.getResponseObject(result, PostTodoRes.class);
        // then
        assertThat(response.getTodoId()).isEqualTo(expected);
    }

    static class REQUEST_URL {
        private static String BASE = "/v2/todo";
        public static String SAVE = BASE;
        public static String UPDATE = BASE;
        public static String RETRIEVE = BASE;
        public static String DELETE = BASE;
    }
}