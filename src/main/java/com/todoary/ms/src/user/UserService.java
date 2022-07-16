package com.todoary.ms.src.user;

import com.todoary.ms.src.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(User user) {
        return this.userDao.insertUser(user);
    }
}
