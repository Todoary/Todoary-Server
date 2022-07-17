package com.todoary.ms.src.user;

import com.todoary.ms.src.user.dto.GetUserRes;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/users")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserProvider userProvider;

    @Autowired
    public UserController(PasswordEncoder passwordEncoder, UserService userService, UserProvider userProvider) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.userProvider = userProvider;
    }

    @GetMapping("/info")
    public BaseResponse<GetUserRes> getUserInfo(HttpServletRequest request) throws BaseException {
        Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
        GetUserRes getUserRes = userProvider.retrieveById(user_id);
        return new BaseResponse<>(getUserRes);
    }



}


