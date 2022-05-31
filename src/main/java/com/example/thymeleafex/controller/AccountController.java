package com.example.thymeleafex.controller;

import com.example.thymeleafex.model.User;
import com.example.thymeleafex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "account/login";
    }

    @GetMapping("/register")
    public String register() {
        return "account/register";
    }
    @PostMapping("/register")
    public String register(User user) {
        userService.save(user);
        return "redirect:/";
    }
    @ResponseBody
    @PostMapping("/check")
    public int check(String username) {
        System.out.println(username);
        int result = userService.checkId(username);
        return result;
    }
}
