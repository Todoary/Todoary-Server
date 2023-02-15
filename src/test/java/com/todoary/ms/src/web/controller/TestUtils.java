package com.todoary.ms.src.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.List;

class TestUtils {
    static BaseResponseStatus getResponseObject(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        return getResponseObject(result, BaseResponseStatus.class);
    }

    static <T> T getResponseObject(MvcResult result, Class<T> type) throws JsonProcessingException, UnsupportedEncodingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return getResponseObject(result, type, objectMapper);
    }

    static <T> T getResponseObject(MvcResult result, Class<T> type, ObjectMapper objectMapper) throws JsonProcessingException, UnsupportedEncodingException {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(BaseResponse.class, type);
        String json = result.getResponse().getContentAsString();
        BaseResponse<T> response = objectMapper.readValue(json, javaType);
        return response.getResult();
    }

    static <T> List<T> getResponseObjectList(MvcResult result, Class<T> type, ObjectMapper objectMapper) throws JsonProcessingException, UnsupportedEncodingException {
        JavaType listType = objectMapper.getTypeFactory().constructParametricType(List.class, type);
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(BaseResponse.class, listType);
        String json = result.getResponse().getContentAsString();
        BaseResponse<List<T>> response = objectMapper.readValue(json, javaType);
        return response.getResult();
    }
}
