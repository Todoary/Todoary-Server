package com.todoary.ms.src.user;

import com.todoary.ms.src.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProvider {

    private final UserDao userDao;

    @Autowired
    public UserProvider(UserDao userDao) {
        this.userDao = userDao;
    }

    public User retrieveByEmail(String email) {
        return userDao.selectByEmail(email);
    }
}
