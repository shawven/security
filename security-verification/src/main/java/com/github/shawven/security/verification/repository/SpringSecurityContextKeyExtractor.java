package com.github.shawven.security.verification.repository;

import com.github.shawven.security.verification.VerificationType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.function.BiFunction;

/**
 * @author Shoven
 * @date 2019-11-20
 */
public class SpringSecurityContextKeyExtractor  {

    public BiFunction<HttpServletRequest, VerificationType, String> get() {
        return (request, type) -> {
            String uniqueId = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof String) {
                    uniqueId = (String)principal;
                } else if (principal instanceof UserDetails) {
                    uniqueId = ((UserDetails) principal).getUsername();
                }
            }
            return uniqueId;
        };
    }
}
