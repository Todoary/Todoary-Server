package com.todoary.ms.src.auth;

import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.auth.model.Token;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthDao authDao;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthService(AuthDao authDao, JwtTokenProvider jwtTokenProvider) {
        this.authDao = authDao;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Long registerRefreshToken(Long userid, String refreshToken) {
        if(authDao.checkUser(userid))
            authDao.updateRefreshToken(userid, refreshToken);
        else
            authDao.insertRefreshToken(userid,refreshToken);

        return userid;
    }

//    public Long modifyRefreshToken(Long userid, String refreshToken) {
//        this.authDao.updateRefreshToken(userid,refreshToken);
//
//        return userid;
//    }

    public Token createAccess(String refreshToken) throws BaseException {

        Long userid = Long.parseLong(jwtTokenProvider.getUseridFromRef(refreshToken));

        String newAccessToken = jwtTokenProvider.createAccessToken(userid);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userid);

        try{
            authDao.updateRefreshToken(userid, newRefreshToken);
        }catch(Exception e){
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

        Token newTokens = new Token(newAccessToken, newRefreshToken);
        return newTokens;
    }

}
