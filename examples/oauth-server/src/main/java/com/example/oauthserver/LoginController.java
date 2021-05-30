package com.example.oauthserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("index")
    public String index() {
        return "index";
    }

    @GetMapping
    public String toSignInPage() {
        return "redirect:/signIn";
    }

    @GetMapping({ "signIn"})
    public String signInPage(HttpServletRequest request, Model model, HttpSession session) {
        return "signIn";
    }

    @GetMapping({ "signUp"})
    public String signUpPage() {
        return "signUp";
    }
}
