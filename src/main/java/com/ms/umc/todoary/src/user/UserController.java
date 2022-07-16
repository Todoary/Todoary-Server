package com.ms.umc.todoary.src.user;

import com.ms.umc.todoary.src.base.BaseException;
import com.ms.umc.todoary.src.base.BaseResponse;
import com.ms.umc.todoary.src.entity.User;
import com.ms.umc.todoary.src.user.model.GetUserProfileRes;
import com.ms.umc.todoary.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
     * 프로필 조회 API
     * [GET] /users?email=
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/users
    public BaseResponse<GetUserProfileRes> getUserProfile() {
        try{
            // filter를 통과했으므로 jwt는 무조건 유효하게 들어있다
            // TODO: 인터셉터에서 넣어주기?
            String jwt = jwtService.getJwt();
            int id = jwtService.getUserId(jwt);
            User user = userProvider.retrieveUserById(id);
            return new BaseResponse<>(new GetUserProfileRes(user.getId(), user.getNickName(), user.getIntroduce(), user.getEmail()));
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
