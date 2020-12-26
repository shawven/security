package com.github.shawven.security.verification.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author Shoven
 * @date 2019-04-23
 */
public interface PhoneUserDetailsService {


    /**
     * 根据手机号登陆
     *
     * @param phone
     * @return
     * @throws UsernameNotFoundException
     */
    UserDetails loadUserByPhone(String phone) throws UsernameNotFoundException;
}
