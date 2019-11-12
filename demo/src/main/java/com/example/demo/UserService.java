package com.example.demo;

import com.github.shawven.security.verification.PhoneUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author Shoven
 * @date 2019-11-12
 */
@Service("userDetailsService")
public class UserService implements UserDetailsService, PhoneUserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return build();
    }

    @Override
    public UserDetails loadUserByPhone(String phone) throws UsernameNotFoundException {
        return build();
    }

    public UserDetails build() {
        return new User("18684844593", "$2a$10$7K/PlNSqdjSSZ7/DxIVrNOqogSra6hzof7MAs9zeUeE517qahqIgy",  Collections.emptyList());
    }
}
