package com.github.shawven.security.verification;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ValidateFailureHandler {

    void handle(HttpServletRequest request, HttpServletResponse response, VerificationException e);
}
