package com.example.demo;

import com.github.shawven.security.verification.security.PhoneUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Shoven
 * @date 2019-11-12
 */
@Service("userDetailsService")
public class UserService implements UserDetailsService, PhoneUserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return build(username);
    }

    @Override
    public UserDetails loadUserByPhone(String phone) throws UsernameNotFoundException {
        return build(phone);
    }

    public UserDetails build(String username) {
        HashMap<String, User> users = new HashMap<>();
        User userInfo = new User("18684844593",
                "$2a$10$7K/PlNSqdjSSZ7/DxIVrNOqogSra6hzof7MAs9zeUeE517qahqIgy",
                Collections.emptyList());
        users.put("18684844593", userInfo);

        User user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return user;
    }
}
