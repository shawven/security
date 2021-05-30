package com.example.oauthserver;

import com.github.shawven.security.server.verification.PhoneUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;

/**
 * @author Shoven
 * @date 2019-11-12
 */
@Service("userDetailsService")
public class UserDetailService implements UserDetailsService, PhoneUserDetailsService, SocialUserDetailsService {


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

    @Override
    public SocialUserDetails loadUserByUserId(String id) throws UsernameNotFoundException {
        HashMap<String, SocialUser> users = new HashMap<>();
        SocialUser userInfo = new SocialUser("1",
                "$2a$10$7K/PlNSqdjSSZ7/DxIVrNOqogSra6hzof7MAs9zeUeE517qahqIgy",
                Collections.emptyList());
        users.put("1", userInfo);
        SocialUser user = users.get(id);
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return user;
    }
}
