package com.ms.umc.todoary.src.login;

import com.ms.umc.todoary.config.BaseException;
import com.ms.umc.todoary.config.secret.Secret;
import com.ms.umc.todoary.jwt.TokenProvider;
import com.ms.umc.todoary.src.login.model.PostLoginReq;
import com.ms.umc.todoary.src.login.model.PostLoginRes;
import com.ms.umc.todoary.src.login.model.*;
import com.ms.umc.todoary.utils.JwtService;
import com.ms.umc.todoary.utils.SHA256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.ms.umc.todoary.config.BaseResponseStatus.*;

@Service
public class LoginProvider {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final LoginDao loginDao;
    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoginProvider(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, LoginDao loginDao, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.loginDao = loginDao;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }
    // user check
    public int checkUserExist(int userIdx) throws BaseException {
        try {
            return loginDao.checkUserExist(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // email check
    public int checkEmailExist(String email) throws BaseException {
        try {
            return loginDao.checkEmailExist(email);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {

//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(postLoginReq.getEmail(), postLoginReq.getPassword());
//        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
        User user = loginDao.getPwd(postLoginReq);
//      String password;
//      String passwordraw;
//        try {
//            password = bCryptPasswordEncoder.encode(postLoginReq.getPassword()); //
//        } catch (Exception ignored) {
//            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
//        }

        if (passwordEncoder.matches(postLoginReq.getPassword(), user.getPassword())) {
            int userIdx = user.getId();
            String jwt = jwtService.createJwt(userIdx);
            System.out.println("success");

            return new PostLoginRes(userIdx, jwt);
        } else {
            throw new BaseException(FAILED_TO_LOGIN);
        }

    }
}
