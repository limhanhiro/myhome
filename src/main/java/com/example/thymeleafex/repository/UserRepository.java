package com.example.thymeleafex.repository;


import com.example.thymeleafex.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long>, QuerydslPredicateExecutor<User>, CustomizedUserRepository {
    @EntityGraph(attributePaths = { "board" }) // FetchType 을 무시하고 다가져온다
    List<User> findAll();

    User findByUsername(String username);


    @Query("select u from User u where u.username like %?1%") // query dsl
    List<User> findByusernmaeQuery(String username);

    @Query(value = "select * from User u where u.username like %?1%" , nativeQuery = true) // query dsl
    List<User> findByusernmaeNativeQuery(String username);


}
