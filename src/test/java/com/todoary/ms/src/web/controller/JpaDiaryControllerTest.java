package com.todoary.ms.src.web.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoary.ms.src.config.auth.WithTodoaryMockUser;
import com.todoary.ms.src.service.JpaDiaryService;
import com.todoary.ms.src.web.dto.*;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.time.YearMonth;
import java.util.List;

import static com.todoary.ms.src.web.controller.TestUtils.getResponseObject;
import static com.todoary.ms.src.web.controller.TestUtils.getResponseObjectList;
import static com.todoary.ms.util.BaseResponseStatus.*;
import static com.todoary.ms.util.ColumnLengthInfo.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JpaDiaryController.class)
@Import(HttpEncodingAutoConfiguration.class)
public class JpaDiaryControllerTest
{


    @Autowired
    MockMvc mvc;

    @MockBean
    private JpaDiaryService diaryService;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    @WithTodoaryMockUser
    void 일기_제목_최대_길이_이하일때_생성O() throws Exception {
        // given
        Long diaryId= 1L;
        given(diaryService.createOrModify(any(), any(), any())).willReturn(diaryId);
        DiarySaveResponse expected=new DiarySaveResponse(diaryId);

        // when
        DiaryRequest requestDto = new DiaryRequest("제목", "내용");
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE_MODIFY).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        DiarySaveResponse response = getResponseObject(result, DiarySaveResponse.class);
        // then
        assertThat(response.getDiaryId()).isEqualTo(expected);
    }



    @Test
    @WithTodoaryMockUser
    void 투두_제목_최대_길이_초과일때_생성X() throws Exception {
        // given
        String title = "오";
        // 최대 길이까지 반복
        int repeatCount = (int) Math.ceil((double) DIARY_TITLE_MAX_LENGTH / getGraphemeLength(title)) + 1;
        title = title.repeat(repeatCount);
        DiaryRequest requestDto = new DiaryRequest(title, "내용");
        // when
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE_MODIFY).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        // then
        AssertionsForClassTypes.assertThat(getResponseObject(result)).isEqualTo(DIARY_TITLE_TOO_LONG);
    }



    @Test
    @WithTodoaryMockUser
    void 일기_제목_최대_길이_이하일때_수정O() throws Exception {
        // given
        doNothing().when(diaryService).createOrModify(any(), any(), any());
        // when
        DiaryRequest requestDto = new DiaryRequest("제목", "내용");
        MvcResult result = mvc.perform(post(REQUEST_URL.SAVE_MODIFY).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new  ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        // then
        AssertionsForClassTypes.assertThat(getResponseObject(result)).isEqualTo(SUCCESS);
    }

    @Test
    @WithTodoaryMockUser
    void 일기_제목이_최대_길이_초과일때_수정X() throws Exception {
        // given
        String title = "제목";
        // 최대 길이까지 반복
        int repeatCount = (int) Math.ceil((double) DIARY_TITLE_MAX_LENGTH / getGraphemeLength(title)) + 1;
        title = title.repeat(repeatCount);
        // when
        DiaryRequest requestDto = new DiaryRequest(title, "내용");
        MvcResult result = mvc.perform(patch(REQUEST_URL.SAVE_MODIFY, 1L).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        // then
        AssertionsForClassTypes.assertThat(getResponseObject(result)).isEqualTo(DIARY_TITLE_TOO_LONG);
    }


    @Test
    @WithTodoaryMockUser
    void 월별_일기_존재_날짜_조회O() throws Exception {
        // given
        YearMonth yearMonth = YearMonth.of(2023, 1);
        List<Integer> expected = List.of(1, 10);
        given(diaryService.findDiaryInMonth(any(), eq(yearMonth))).willReturn(expected);
        // when
        MvcResult result = mvc.perform(get(REQUEST_URL.RETRIEVE_MONTH_DAYS, yearMonth))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        List<Integer> response = getResponseObjectList(result, Integer.class, objectMapper);
        // then
        assertThat(response).containsAll(expected);
    }




    static class REQUEST_URL {
        private static final String BASE = "/v2/diary";
        public static final String SAVE_MODIFY = BASE + "/{createdDate}";
        public static final String RETRIEVE_MONTH_DAYS = BASE + "/days/{yearMonth}";

    }


}
