package com.todoary.ms.src.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class ErrorLogWriter {
    private static final Logger log = LoggerFactory.getLogger(ErrorLogWriter.class.getSimpleName());

    /**
     * jwt 헤더가 포함된 request 로그를 남길 때 user id 정보를 로그에 같이 남긴다.
     * 로그에 request 정보와 그 때의 exception 메세지를 남긴다.
     *
     * @param exception
     * @param request
     */
    public static void writeExceptionWithAuthorizedRequest(Exception exception, HttpServletRequest request) {
        log.error("{} - user id: {} | uri: {} {} | query string: {}", exception.getMessage(), request.getAttribute("user_id").toString(),
                request.getMethod(), request.getRequestURI(), parameterMapToString(request.getParameterMap()));
    }

    /**
     * jwt 헤더가 포함된 request 로그를 남길 때 user id 정보를 로그에 같이 남긴다.
     * 로그에 request 정보와 그 때의 exception 메세지, body를 남긴다.
     *
     * @param exception
     * @param request
     * @param messageBody
     */
    public static void writeExceptionWithAuthorizedRequest(Exception exception, HttpServletRequest request, String messageBody) {
        log.error("{} - user id: {} | uri: {} {} | query string: {} | body: {}", exception.getMessage(), request.getAttribute("user_id").toString(),
                request.getMethod(), request.getRequestURI(), parameterMapToString(request.getParameterMap()), messageBody);
    }

    /**
     * 로그에 request 정보, exception 메세지를 남긴다.
     *
     * @param exception
     * @param request
     */
    public static void writeExceptionWithRequest(String causedMethodName, Exception exception, HttpServletRequest request) {
        log.error("[{}] {} - Authorization: {} | uri: {} {} | query string: {}", causedMethodName, exception.getMessage(), request.getHeader("Authorization"),
                request.getMethod(), request.getRequestURI(), parameterMapToString(request.getParameterMap()));
    }

    /**
     * 로그에 request 정보, exception 메세지, body를 남긴다.
     *
     * @param exception
     * @param request
     * @param messageBody
     */
    public static void writeExceptionWithRequest(String causedMethodName, Exception exception, HttpServletRequest request, String messageBody) {
        log.error("[{}] {} - Authorization: {} | uri: {} {} | query string: {} | body: {}", causedMethodName, exception.getMessage(), request.getHeader("Authorization"),
                request.getMethod(), request.getRequestURI(), parameterMapToString(request.getParameterMap()), messageBody);
    }

    /**
     * 로그에 메세지와 그 때의 exception 메세지를 남긴다
     *
     * @param exception
     * @param message   로그에 남길 메세지
     */
    public static void writeExceptionWithMessage(Exception exception, String message) {
        log.error("{} - {}", exception.getMessage(), message);
    }

    public static String parameterMapToString(Map<String, String[]> parameters) {
        return parameters.entrySet().stream()
                .map(p -> p.getKey() + "=" + String.join(",", p.getValue()))
                .reduce((p1, p2) -> p1 + "&" + p2)
                .orElse("");
    }
}
