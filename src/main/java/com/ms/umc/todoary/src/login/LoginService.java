package com.ms.umc.todoary.src.login;

import com.ms.umc.todoary.config.BaseException;
import com.ms.umc.todoary.config.BaseResponseStatus;
import com.ms.umc.todoary.src.login.model.PostLoginReq;
import com.ms.umc.todoary.src.login.model.PostLoginRes;
import com.ms.umc.todoary.src.login.model.User;
import com.ms.umc.todoary.utils.JwtService;
import com.ms.umc.todoary.utils.SHA256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ms.umc.todoary.config.BaseResponseStatus.*;

@Service
public class LoginService {
    private final LoginDao loginDao;
    private final JwtService jwtService;

    @Autowired
    public LoginService(LoginDao loginDao, JwtService jwtService) {
        this.loginDao = loginDao;
        this.jwtService = jwtService;
    }


    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
        User user = loginDao.selectUser(postLoginReq);
        String encryptPassword;

        try {
            encryptPassword = new SHA256().encrypt(postLoginReq.getPassword());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }


        if (postLoginReq.getPassword().equals(user.getPassword())) {
            int id = user.getId();
            String jwt = encryptPassword;

            if (postLoginReq.is_isAutoLoginChecked() == true)
                return new PostLoginRes(id, jwt);
            else
                return new PostLoginRes(id, null);
        } else {
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }
}
