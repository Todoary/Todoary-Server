package com.todoary.ms.src.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoary.ms.src.auth.dto.*;
import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.auth.model.PrincipalDetails;
import com.todoary.ms.src.auth.model.Token;
import com.todoary.ms.src.user.UserProvider;
import com.todoary.ms.src.user.UserService;
import com.todoary.ms.src.user.dto.PostUserReq;
import com.todoary.ms.src.user.model.User;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;



import static com.todoary.ms.util.BaseResponseStatus.*;

import static com.todoary.ms.util.Secret.JWT_REFRESH_SECRET_KEY;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserProvider userProvider;
    private final AuthService authService;
    private final AuthProvider authProvider;


    @Autowired
    public AuthController(PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, UserService userService, UserProvider userProvider, AuthService authService, AuthProvider authProvider) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userProvider = userProvider;
        this.authService = authService;
        this.authProvider = authProvider;
    }

    @GetMapping("/login/success")
    public BaseResponse<PostLoginRes> loginSuccess(@RequestParam("accessToken") String accessToken, @RequestParam("refreshToken") String refreshToken) {
        Token token = new Token(accessToken, refreshToken);

        PostLoginRes postLoginRes = new PostLoginRes(token);
        return new BaseResponse<>(postLoginRes);
    }


    @PostMapping("/signup")
    public BaseResponse<String> postUser(@RequestBody PostUserReq postUserReq) {
        try {
            String encodedPassword = passwordEncoder.encode(postUserReq.getPassword());
            User user = new User(postUserReq.getUsername(), postUserReq.getNickname(), postUserReq.getEmail(), encodedPassword, "ROLE_USER", "none", "none");
            userService.createUser(user);
            return new BaseResponse("회원가입에 성공했습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    // @PostMapping("/signin")
    // public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq) {
    //     User user = null;
    //     try {
    //         user = userProvider.retrieveByEmail(postLoginReq.getEmail());
    //     } catch (BaseException e) {
    //         e.printStackTrace();
    //     }
    //     PrincipalDetails authenticatedUser = attemptAuthentication(user);
    //
    //     Long userid = principalDetails.getUser().getId();
    //     String accessToken = jwtTokenProvider.createAccessToken(userid);
    //     String refreshToken = jwtTokenProvider.createRefreshToken(userid);
    //
    //     authService.createRefreshToken(userid, refreshToken);
    //
    //     Token token = new Token(accessToken, refreshToken);
    //     PostLoginRes postLoginRes = new PostLoginRes(token);
    //
    //
    // }
//    @PostMapping("/signin/auto")
//    public BaseResponse<PostAutoLoginRes> autoLogin(@RequestBody PostAutoLoginReq postAutoLoginReq) {
//
//    }

    @PostMapping("/jwt")
    public BaseResponse<PostAccessRes> postAccess(@RequestBody PostAccessReq postAccessReq) {
        String refreshToken = postAccessReq.getRefreshToken();
        try {
            isRefreshTokenEqualAndValid(refreshToken);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }


        Token newTokens = authService.createAccess(refreshToken);

        PostAccessRes postAccessRes = new PostAccessRes(newTokens);
        return new BaseResponse<>(postAccessRes);
    }


    public boolean isRefreshTokenEqualAndValid(String token) throws BaseException {
        try {
            Jwts
                    .parser()
                    .setSigningKey(JWT_REFRESH_SECRET_KEY)
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new BaseException(EXPIRED_JWT);
        } catch (Exception e) {
            throw new BaseException(INVALID_JWT);
        }

        if(!authProvider.isRefreshTokenEqual(token))
            return false;
        return true;
    }

}
