package com.todoary.ms.src.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoary.ms.src.category.dto.PostCategoryRes;
import com.todoary.ms.src.service.JpaCategoryService;
import com.todoary.ms.src.web.dto.CategoryRequest;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static com.todoary.ms.util.BaseResponseStatus.CATEGORY_TITLE_TOO_LONG;
import static com.todoary.ms.util.BaseResponseStatus.EMPTY_COLOR_CATEGORY;
import static com.todoary.ms.util.ColumnLengthInfo.CATEGORY_TITLE_MAX_LENGTH;
import static com.todoary.ms.util.ColumnLengthInfo.getGraphemeLength;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JpaCategoryController.class)
class JpaCategoryControllerTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    private JpaCategoryService categoryService;

    @Test
    @WithMockUser
    void 카테고리_제목이_최대_길이_이하일때_생성O() throws Exception {
        // given
        Long categoryId = 1L;
        given(categoryService.saveCategory(any(), any())).willReturn(categoryId);
        PostCategoryRes expected = new PostCategoryRes(categoryId);
        // when
        CategoryRequest requestDto = new CategoryRequest("카테고리", 10);
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).requestAttr("user_id", 10L).with(csrf())
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        PostCategoryRes response = getResponseObject(result, PostCategoryRes.class);
        // then
        assertThat(response).isEqualTo(expected);
    }

    @Test
    @WithMockUser
    void 카테고리_제목이_최대_길이_초과일때_생성X() throws Exception {
        // given
        String title = "오";
        // 최대 길이까지 반복
        int repeatCount = (int) Math.ceil((double) CATEGORY_TITLE_MAX_LENGTH / getGraphemeLength(title)) + 1;
        title = title.repeat(repeatCount);
        CategoryRequest requestDto = new CategoryRequest(title, 10);
        // when
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).requestAttr("user_id", 10L).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result, BaseResponseStatus.class);
        // then
        assertThat(status).isEqualTo(CATEGORY_TITLE_TOO_LONG);
    }

    @Test
    @WithMockUser
    void 카테고리_색상이_null일때_생성X() throws Exception {
        // given
        CategoryRequest requestDto = new CategoryRequest("title", null);
        // when
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE).requestAttr("user_id", 10L).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result, BaseResponseStatus.class);
        // then
        assertThat(status).isEqualTo(EMPTY_COLOR_CATEGORY);
    }

    @Test
    @WithMockUser
    void 카테고리_제목이_최대_길이_이하일때_수정O() throws Exception {
        // given
        doNothing().when(categoryService).updateCategory(any(), any(), any());
        // when
        CategoryRequest requestDto = new CategoryRequest("카테고리", 10);
        mvc.perform(patch(REQUEST_URL.UPDATE, 1L).requestAttr("user_id", 10L).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print());
        // then nothing thrown
    }

    @Test
    @WithMockUser
    void 카테고리_제목이_최대_길이_초과일때_수정X() throws Exception {
        // given
        String title = "오";
        // 최대 길이까지 반복
        int repeatCount = (int) Math.ceil((double) CATEGORY_TITLE_MAX_LENGTH / getGraphemeLength(title)) + 1;
        title = title.repeat(repeatCount);
        // when
        CategoryRequest requestDto = new CategoryRequest(title, 10);
        MvcResult result = mvc.perform(patch(REQUEST_URL.UPDATE, 1L).requestAttr("user_id", 10L).with(csrf())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result, BaseResponseStatus.class);
        // then
        assertThat(status).isEqualTo(CATEGORY_TITLE_TOO_LONG);
    }

    @Test
    @WithMockUser
    void 카테고리_색상이_null일때_수정X() throws Exception {
        // given
        CategoryRequest requestDto = new CategoryRequest("title", null);
        // when
        MvcResult result = mvc.perform(patch(REQUEST_URL.UPDATE, 10).requestAttr("user_id", 10L).with(csrf())
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        BaseResponseStatus status = getResponseObject(result, BaseResponseStatus.class);
        // then
        assertThat(status).isEqualTo(EMPTY_COLOR_CATEGORY);
    }

    static <T> T getResponseObject(MvcResult result, Class<T> type) throws JsonProcessingException, UnsupportedEncodingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(BaseResponse.class, type);
        String json = result.getResponse().getContentAsString();
        BaseResponse<T> response = objectMapper.readValue(json, javaType);
        return response.getResult();
    }

    private static class REQUEST_URL {
        public static String SAVE = "/v2/category";
        public static String UPDATE = "/v2/category/{categoryId}";
    }
}