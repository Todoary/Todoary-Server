package com.ms.umc.todoary.src.auth;

import com.ms.umc.todoary.src.base.BaseException;
import com.ms.umc.todoary.src.base.BaseResponse;
import com.ms.umc.todoary.src.auth.model.*;

import com.ms.umc.todoary.src.base.BaseResponseStatus;
import com.ms.umc.todoary.src.entity.PrincipalDetails;
import com.ms.umc.todoary.src.entity.User;
import com.ms.umc.todoary.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.ms.umc.todoary.utils.ValidationRegex.isRegexEmail;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    @Autowired
    public AuthController(AuthService authService, JwtService jwtService, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    // // 일반/자동 로그인
    // @PostMapping("/login")
    // @ResponseBody
    // public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) throws BaseException {
    //     // 형식적 validation -> 존재하는지 일단 확인
    //     try{
    //         log.info(postLoginReq.getEmail() + " | "+postLoginReq.getPassword());
    //         // 1. 이메일 검증
    //         // 1-1. 이메일 입력 안했을 때
    //         if (postLoginReq.getEmail() == null || postLoginReq.getEmail().length() == 0){
    //             return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
    //         }
    //         // 1-2. 이메일 형식 검증 (정규식)
    //         if(!isRegexEmail(postLoginReq.getEmail())){
    //             return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
    //         }
    //         // 2. 패스워드 검증
    //         // 2-1. 패스워드 입력 안했을 때
    //         if(postLoginReq.getPassword() == null || postLoginReq.getPassword().length() == 0){
    //             return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_PASSWORD);
    //         }
    //         // 2-2. 패스워드 형식 검증 (정규식)
    //         /*
    //          * 비밀번호 같은 경우엔 형식이 틀리면 어차피 비밀번호가 없으므로 검사를 할 수도 있고 안할 수도 있다
    //          * 그 때 그때 알맞게 판단해서 쓸 것
    //          * */
    //         PostLoginRes postLoginRes = authService.logIn(postLoginReq);
    //         return new BaseResponse<>(postLoginRes);
    //     }catch(BaseException exception){
    //         return new BaseResponse<>(exception.getStatus());
    //     }
    // }

    @ResponseBody
    @PostMapping("/signUp")
    public BaseResponse<PostUserRes> postUser(@RequestBody PostUserReq postUserReq) {
        try{
            PostUserRes postUserRes = authService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
