package com.todoary.ms.src.auth;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

import static com.todoary.ms.util.BaseResponseStatus.EXPIRED_JWT;
import static com.todoary.ms.util.BaseResponseStatus.INVALID_JWT;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserProvider userProvider;
    private final AuthService authService;
    private final AuthProvider authProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    public AuthController(PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, UserService userService, UserProvider userProvider, AuthService authService, AuthProvider authProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userProvider = userProvider;
        this.authService = authService;
        this.authProvider = authProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
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

     @PostMapping("/signin")
     public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq) {
         User user = null;
         try {
             user = userProvider.retrieveByEmail(postLoginReq.getEmail());
             user.setPassword(postLoginReq.getPassword());
         } catch (BaseException e) {
             return new BaseResponse(e.getStatus());
         }

         Authentication authentication = attemptAuthentication(user);
         System.out.println(authentication.getPrincipal());
         PrincipalDetails userEntity = (PrincipalDetails) authentication.getPrincipal();
         SecurityContextHolder.getContext().setAuthentication(authentication);
         Long user_id = userEntity.getUser().getId();
         String accessToken = jwtTokenProvider.createAccessToken(user_id);
         Token token = new Token(accessToken, "");
         PostLoginRes postLoginRes = new PostLoginRes(token);

         return new BaseResponse<>(postLoginRes);
     }
    @PostMapping("/signin/auto")
    public BaseResponse<PostAutoLoginRes> autoLogin(@RequestBody PostAutoLoginReq postAutoLoginReq) {
        User user = null;
        try {
            user = userProvider.retrieveByEmail(postAutoLoginReq.getEmail());
            user.setPassword(postAutoLoginReq.getPassword());
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
        Authentication authentication = attemptAuthentication(user);
        PrincipalDetails userEntity = (PrincipalDetails) authentication.getPrincipal();

        Long user_id = userEntity.getUser().getId();

        String accessToken = jwtTokenProvider.createAccessToken(user_id);
        String refreshToken = jwtTokenProvider.createRefreshToken(user_id);

        authService.registerRefreshToken(user_id, refreshToken);

        Token token = new Token(accessToken, refreshToken);
        PostAutoLoginRes postAutoLoginRes = new PostAutoLoginRes(token);

        return new BaseResponse<>(postAutoLoginRes);
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
                    .parserBuilder().setSigningKey(jwtTokenProvider.getAccessKey()).build()
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

    public Authentication attemptAuthentication(User user) {
        Collection<GrantedAuthority> userAuthorities = new ArrayList<>();
        userAuthorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), userAuthorities);
        System.out.println(user.getPassword());
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }
}
