package com.github.shawven.security.verification;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ValidateSuccessHandler {

    void handle(HttpServletRequest request, HttpServletResponse response);
}
