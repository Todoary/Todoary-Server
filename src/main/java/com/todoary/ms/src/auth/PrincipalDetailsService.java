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
        User user = null;
        try {
            user = userProvider.retrieveByEmail(email);
        } catch (BaseException e) {
            e.printStackTrace();
        }

        if (user != null)
            return new PrincipalDetails(user);
        return null;
    }
}
