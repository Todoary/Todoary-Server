package com.todoary.ms.src.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoary.ms.src.web.dto.category.CategorySaveResponse;
import com.todoary.ms.src.config.auth.WithTodoaryMockUser;
import com.todoary.ms.src.service.JpaCategoryService;
import com.todoary.ms.src.web.dto.category.CategoryRequest;
import com.todoary.ms.src.web.dto.category.CategoryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.stream.IntStream;

import static com.todoary.ms.src.web.controller.TestUtils.getResponseObject;
import static com.todoary.ms.src.common.response.BaseResponseStatus.*;
import static com.todoary.ms.src.common.util.ColumnLengthInfo.CATEGORY_TITLE_MAX_LENGTH;
import static com.todoary.ms.src.common.util.ColumnLengthInfo.getGraphemeLength;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JpaCategoryController.class)
// MvcResult 한글 깨짐
@Import(HttpEncodingAutoConfiguration.class)
class JpaCategoryControllerTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    private JpaCategoryService categoryService;

    @Test
    @WithTodoaryMockUser
    void 카테고리_제목이_최대_길이_이하일때_생성O() throws Exception {
        // given
        Long categoryId = 1L;
        given(categoryService.saveCategory(any(), any())).willReturn(categoryId);
        CategorySaveResponse expected = new CategorySaveResponse(categoryId);
        // when
        CategoryRequest requestDto = new CategoryRequest("카테고리", 10);
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        CategorySaveResponse response = getResponseObject(result, CategorySaveResponse.class);
        // then
        assertThat(response).isEqualTo(expected);
    }

    @Test
    @WithTodoaryMockUser
    void 카테고리_제목이_최대_길이_초과일때_생성X() throws Exception {
        // given
        String title = "오";
        // 최대 길이까지 반복
        int repeatCount = (int) Math.ceil((double) CATEGORY_TITLE_MAX_LENGTH / getGraphemeLength(title)) + 1;
        title = title.repeat(repeatCount);
        CategoryRequest requestDto = new CategoryRequest(title, 10);
        // when
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        // then
        assertThat(getResponseObject(result)).isEqualTo(CATEGORY_TITLE_TOO_LONG);
    }

    @Test
    @WithTodoaryMockUser
    void 카테고리_색상이_null일때_생성X() throws Exception {
        // given
        CategoryRequest requestDto = new CategoryRequest("title", null);
        // when
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        // then
        assertThat(getResponseObject(result)).isEqualTo(EMPTY_COLOR_CATEGORY);
    }

    @Test
    @WithTodoaryMockUser
    void 카테고리_제목이_최대_길이_이하일때_수정O() throws Exception {
        // given
        doNothing().when(categoryService).updateCategory(any(), any(), any());
        // when
        CategoryRequest requestDto = new CategoryRequest("카테고리", 10);
        MvcResult result = mvc.perform(patch(REQUEST_URL.UPDATE, 1L).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        // then
        assertThat(getResponseObject(result)).isEqualTo(SUCCESS);
    }

    @Test
    @WithTodoaryMockUser
    void 카테고리_제목이_최대_길이_초과일때_수정X() throws Exception {
        // given
        String title = "오";
        // 최대 길이까지 반복
        int repeatCount = (int) Math.ceil((double) CATEGORY_TITLE_MAX_LENGTH / getGraphemeLength(title)) + 1;
        title = title.repeat(repeatCount);
        // when
        CategoryRequest requestDto = new CategoryRequest(title, 10);
        MvcResult result = mvc.perform(patch(REQUEST_URL.UPDATE, 1L).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        // then
        assertThat(getResponseObject(result)).isEqualTo(CATEGORY_TITLE_TOO_LONG);
    }

    @Test
    @WithTodoaryMockUser
    void 카테고리_색상이_null일때_수정X() throws Exception {
        // given
        CategoryRequest requestDto = new CategoryRequest("title", null);
        // when
        MvcResult result = mvc.perform(patch(REQUEST_URL.UPDATE, 10).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        // then
        assertThat(getResponseObject(result)).isEqualTo(EMPTY_COLOR_CATEGORY);
    }

    @Test
    @WithTodoaryMockUser
    void 카테고리_조회O() throws Exception {
        // given
        CategoryResponse[] expected = IntStream.range(0, 5)
                .mapToObj(i -> new CategoryResponse((long) i, "카테고리" + i, i))
                .toArray(CategoryResponse[]::new);
        given(categoryService.findCategories(any())).willReturn(expected);
        // when
        MvcResult result = mvc.perform(get(REQUEST_URL.RETRIEVE))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        CategoryResponse[] responses = getResponseObject(result, CategoryResponse[].class);
        // then
        assertThat(responses).containsExactly(expected);
    }

    @Test
    @WithTodoaryMockUser
    void id가_null이_아닐_때_카테고리_삭제O() throws Exception {
        // given
        Long categoryId = 1L;
        doNothing().when(categoryService).deleteCategory(any(), any());
        // when
        MvcResult result = mvc.perform(delete(REQUEST_URL.DELETE, categoryId).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        // then
        assertThat(getResponseObject(result)).isEqualTo(SUCCESS);
    }

    private static class REQUEST_URL {
        public static String BASE = "/jpa/category";
        public static String SAVE = BASE;
        public static String UPDATE = BASE + "/{categoryId}";
        public static String RETRIEVE = BASE;
        public static String DELETE = BASE + "/{categoryId}";
    }
}