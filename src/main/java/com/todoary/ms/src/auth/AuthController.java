package com.todoary.ms.src.auth;

import com.todoary.ms.src.auth.dto.PostAccessReq;
import com.todoary.ms.src.auth.dto.PostAccessRes;
import com.todoary.ms.src.auth.dto.PostLoginRes;
import com.todoary.ms.src.auth.model.Token;
import com.todoary.ms.src.user.UserService;
import com.todoary.ms.src.user.dto.PostUserReq;
import com.todoary.ms.src.user.dto.PostUserRes;
import com.todoary.ms.src.user.model.User;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;



import static com.todoary.ms.util.BaseResponseStatus.*;

import static com.todoary.ms.util.Secret.JWT_REFRESH_SECRET_KEY;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthService authService;
    private final AuthProvider authProvider;

    @Autowired
    public AuthController(PasswordEncoder passwordEncoder, UserService userService, AuthService authService, AuthProvider authProvider) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
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
    public BaseResponse<PostUserRes> postUser(@RequestBody PostUserReq postUserReq) {
        String encodedPassword = passwordEncoder.encode(postUserReq.getPassword());
        User user = new User(postUserReq.getUsername(), postUserReq.getNickname(), postUserReq.getEmail(), encodedPassword, "ROLE_USER","none","none");
        user = userService.createUser(user);

        PostUserRes postUserRes = new PostUserRes(user.getUsername(), user.getNickname(), user.getEmail());
        return new BaseResponse(postUserRes);
    }

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
