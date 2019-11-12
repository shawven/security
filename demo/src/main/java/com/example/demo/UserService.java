package com.example.demo;

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
public class UserService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return new User("18684844593", "$2a$10$7K/PlNSqdjSSZ7/DxIVrNOqogSra6hzof7MAs9zeUeE517qahqIgy",  Collections.emptyList());
    }
}
