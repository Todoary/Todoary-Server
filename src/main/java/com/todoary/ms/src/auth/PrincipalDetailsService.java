package com.todoary.ms.src.auth;

import com.todoary.ms.src.auth.model.PrincipalDetails;
import com.todoary.ms.src.user.UserProvider;
import com.todoary.ms.src.user.model.User;
import com.todoary.ms.util.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PrincipalDetailsService implements UserDetailsService {

    private final UserProvider userProvider;

    @Autowired
    public PrincipalDetailsService(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            System.out.println("loadUserByUsername - email");
            return new PrincipalDetails(userProvider.retrieveByEmail(email));
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }
    }

//    public UserDetails loadUserByUsername(Long user_id) {
//        try {
//            System.out.println("loadUserByUsername - user_id");
//            return new PrincipalDetails(userProvider.retrieveById(user_id));
//        } catch (BaseException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
