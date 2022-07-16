package com.ms.umc.todoary.src.user;

import com.ms.umc.todoary.src.base.BaseException;
import com.ms.umc.todoary.src.base.BaseResponse;
import com.ms.umc.todoary.src.entity.User;
import com.ms.umc.todoary.src.user.model.GetUserRes;
import com.ms.umc.todoary.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.ms.umc.todoary.src.base.BaseResponseStatus.POST_USERS_EMPTY_EMAIL;
import static com.ms.umc.todoary.src.base.BaseResponseStatus.POST_USERS_INVALID_EMAIL;
import static com.ms.umc.todoary.utils.ValidationRegex.isRegexEmail;


@RestController
@RequestMapping("/users")
public class UserController {


    private final UserProvider userProvider;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public UserController( UserProvider userProvider, UserService userService, JwtService jwtService) {

        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 이메일 검색 조회 API
     * [GET] /users?email=
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/users
    public BaseResponse<GetUserRes> getUsers(@RequestParam(required = true) String email) {
        try{
            if(email.length()==0){
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            // 이메일 정규표현
            if(!isRegexEmail(email)){
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            User user = userProvider.getUserByEmail(email);
            return new BaseResponse<>(new GetUserRes(user.getId(), user.getName(), user.getNickname(), user.getEmail()));
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
