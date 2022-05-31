package com.example.thymeleafex.controller;

import com.example.thymeleafex.model.Board;
import com.example.thymeleafex.model.QUser;
import com.example.thymeleafex.model.User;
import com.example.thymeleafex.repository.UserRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.aspectj.weaver.ast.ITestVisitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
class UserApiController {

    @Autowired
    private UserRepository repository;


    @GetMapping("/user")
    Iterable<User> all(@RequestParam(required = false) String method,@RequestParam(required = false) String text){

        Iterable<User> user = null;
        if("query".equals(method)) {
            user = repository.findByusernmaeQuery(text);
        }else if("nativequery".equals(method)){
            user = repository.findByusernmaeNativeQuery(text);
        }else if("querydsl".equals(method)){
            QUser users = QUser.user;
            Predicate predicate = users.username.contains(text);
            user = repository.findAll(predicate);
        }else if("querydslCustom".equals(method)){
            user = repository.findByUsernameCustom(text);
        }else if("jdbc".equals(method)){
            user = repository.findByUsernameJdbc(text);
        }else{
            user = repository.findAll();
        }
        return user;
    }
    // end::get-aggregate-root[]

    @PostMapping("/user")
    User newUser(@RequestBody User newUser) {
        return repository.save(newUser);
    }

    // Single item

    @GetMapping("/user/{id}")
    User one(@PathVariable Long id) {

        return repository.findById(id).orElse(null);

    }

    @PutMapping("/user/{id}")
    User replaceUser(@RequestBody User newUser, @PathVariable Long id) {

        return repository.findById(id)
                .map(user -> {
//                    user.setTitle(newUser.getTitle());
//                    user.setContent(newUser.getContent());
                    user.setBoard(newUser.getBoard());
                    for(Board board : user.getBoard()){
                        board.setUser(user);
                    }
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setId(id);
                    return repository.save(newUser);
                });
    }

    @DeleteMapping("/user/{id}")
    void deleteUser(@PathVariable Long id) {
        repository.deleteById(id);
    }
}