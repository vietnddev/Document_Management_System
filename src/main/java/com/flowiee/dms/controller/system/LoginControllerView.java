package com.flowiee.dms.controller.system;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class LoginControllerView {
    @GetMapping("/sys/login")
    public ModelAndView showLoginPage() {
        return new ModelAndView("login");
    }
}