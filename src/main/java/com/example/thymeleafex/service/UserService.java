package com.example.thymeleafex.service;

import com.example.thymeleafex.model.Role;
import com.example.thymeleafex.model.User;
import com.example.thymeleafex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User save(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword()); // 입력받음 암호를 passwordEncoder를 이용하여 암호화
        user.setPassword(encodedPassword); // 암호화한 비밀 번호를 다시 세팅
        user.setEnabled(true);
        Role role = new Role();
        role.setId(1);
        user.getRole().add(role); //user_role table에 아이디와 권한을 추가한다
        return userRepository.save(user);
    }

    public int checkId(String username) {
        List<User> user =  userRepository.findByUsernameCustom(username);
        return user.toArray().length;
    }
}
