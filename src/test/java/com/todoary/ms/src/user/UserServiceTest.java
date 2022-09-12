package com.todoary.ms.src.user;

import com.todoary.ms.util.BaseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Test
    void modifyFcmToken() throws BaseException {
        userService.modifyFcmToken(92L,"ehBE6fAkpkGpiqc7IvYJmT:APA91bEy_IaJ0PerpdDWdapUMBw8XzF3yqatDisPIYykg0BJuZQQ2LgtKZAvziLc9ucldKTTbx_rY-bnQ297Wg5IpOJ7Ki8JWvKlcegmrMbryr20X1hAbjBeO7QNl63YnZ5CtjDjnLzA");
    }
}