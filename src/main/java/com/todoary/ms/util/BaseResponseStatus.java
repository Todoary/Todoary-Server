package com.todoary.ms.util;

import lombok.Getter;

import static com.todoary.ms.util.ColumnLengthInfo.CATEGORY_TITLE_MAX_LENGTH;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    EXPIRED_JWT(false, 2003, "만료된 JWT입니다."),
    INVALID_USER_JWT(false, 2004, "권한이 없는 유저의 접근입니다."),
    INVALID_AUTH(false, 2005, "유효하지 않은 회원 정보입니다."),
    DIFFERENT_REFRESH_TOKEN(false, 2006, "유저 정보와 일치하지 않는 refresh token입니다."),
    APPLE_Client_SECRET_ERROR(false, 2007, "클라이언트 시크릿 생성에 실패하였습니다."),
    INVALID_APPLE_AUTH(false, 2008, "유효하지 않은 토큰 입니다."),
    PARSE_USER_ERROR(false, 2009, "애플 유저 조회에 실패하였습니다."),

    EMPTY_USER(false, 2010, "회원 정보가 존재하지 않습니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),
    USERS_EMPTY_USER_EMAIL(false, 2011, "유저 이메일 값을 확인해주세요."),
    USERS_DELETED_USER(false, 2012, "삭제된 유저입니다."),
    USERS_DISACCORD_PASSWORD(false, 2112, "비밀번호가 일치하지 않습니다"),
    USERS_REFRESH_TOKEN_NOT_EXISTS(false, 2113, "유저 정보와 일치하는 Refresh Token이 없습니다."),
    USERS_AUTHENTICATION_FAILURE(false, 2114, "유저 인증을 실패했습니다."),

    // 데이터가 정해진 형식보다 길 때
    DATA_TOO_LONG(false, 2101, "제한 길이를 초과했습니다."),

    // category 도메인 에러
    DUPLICATE_CATEGORY(false, 2104, "같은이름의 카테고리가 이미 존재합니다."),

    CATEGORY_TITLE_TOO_LONG(false, 2105, "카테고리 제목이 제한길이 이상입니다. ("+CATEGORY_TITLE_MAX_LENGTH+" 글자까지 가능)"),

    EMPTY_COLOR_CATEGORY(false, 2106, "카테고리 색상을 입력해주세요."),

    INVALID_PROVIDER(false, 2013, "올바르지 않은 provider입니다. (예: google)"),

    // 300대 -> 3. todo 도메인 에러
    USERS_CATEGORY_NOT_EXISTS(false, 2301, "해당하는 유저와 일치하는 카테고리가 없습니다."),
    USERS_TODO_NOT_EXISTS(false, 2302, "해당하는 유저와 일치하는 투두가 없습니다."),

    // [POST] /users
    POST_USERS_EXISTS_EMAIL(false, 2017, "중복된 이메일입니다."),

    // diary 도메인 에러
    USERS_DIARY_NOT_EXISTS(false, 2402, "해당하는 유저와 일치하는 일기가 없습니다."),

    POST_USERS_EXISTS_NICKNAME(false, 2032, "중복된 닉네임입니다."),
    /**
     * 3000 : Response 오류
     */

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),

    INTERNAL_SERVER_ERROR(false, 4004, "서버에서 에러가 발생했습니다"),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_FCMTOKEN(false, 4015, "FCM Token 수정 실패"),


    // 5000 : AWS관련 오류
    AWS_ACCESS_DENIED(false, 5001, "접근 권한이 없습니다."),
    AWS_FILE_NOT_FOUND(false, 5002, "파일 키에 해당하는 파일이 존재하지 않습니다."),
    AWS_FILE_CONVERT_FAIL(false, 5003, "파일 변환에 실패했습니다."),

    // 6000 : Firebase 관련 오류
    FCM_MESSAGE_PARSING_FAILURE(false, 6001, "FCM 메시지 변환에 실패했습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
