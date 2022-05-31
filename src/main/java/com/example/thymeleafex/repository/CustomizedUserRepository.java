package com.example.thymeleafex.repository;

import com.example.thymeleafex.model.User;

import java.util.List;

public interface CustomizedUserRepository {
    List<User> findByUsernameCustom(String username);

    List<User> findByUsernameJdbc(String username);
}
