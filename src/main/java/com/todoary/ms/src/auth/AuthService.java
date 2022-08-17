package com.todoary.ms.src.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.auth.model.AppleUserInfo;
import com.todoary.ms.src.auth.model.Token;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

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
        if (authDao.checkUser(userid))
            authDao.updateRefreshToken(userid, refreshToken);
        else
            authDao.insertRefreshToken(userid, refreshToken);

        return userid;
    }

//    public Long modifyRefreshToken(Long userid, String refreshToken) {
//        this.authDao.updateRefreshToken(userid,refreshToken);
//
//        return userid;
//    }

    public Token registerNewTokenFromRefreshToken(String refreshToken) throws BaseException {
        Long userId = Long.parseLong(jwtTokenProvider.getUserIdFromRefreshToken(refreshToken));
        return registerNewTokenForUser(userId);
    }

    public Token registerNewTokenForUser(Long userId) throws BaseException {
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        try {
            registerRefreshToken(userId, newRefreshToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

        Token newTokens = new Token(newAccessToken, newRefreshToken);
        return newTokens;
    }

    public AppleUserInfo parseUser(String userInfo) throws BaseException {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject)parser.parse(userInfo);
            Gson gson =new Gson();
            HashMap userMap =new HashMap();
            userMap = (HashMap)gson.fromJson(obj, userMap.getClass());
            HashMap<String, Object> nameMap = objectMapper.convertValue(userMap.get("name"), HashMap.class);
            String name = nameMap.get("lastName")+nameMap.get("firstName").toString();
            String email = userMap.get("email").toString();

            AppleUserInfo appleUserInfo = new AppleUserInfo(name, email);
            System.out.println(appleUserInfo);
            return appleUserInfo;


        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.PARSE_USER_ERROR);
        }
    }

}
