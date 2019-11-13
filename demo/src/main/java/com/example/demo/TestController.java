package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Shoven
 * @date 2019-11-13
 */
@RestController
public class TestController {

    @GetMapping("/me")
    public ResponseEntity index(Authentication authentication) {
        return ResponseEntity.ok(authentication);
    }
}
