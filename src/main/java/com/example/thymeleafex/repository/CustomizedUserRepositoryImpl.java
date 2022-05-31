package com.example.thymeleafex.repository;

import com.example.thymeleafex.model.QUser;
import com.example.thymeleafex.model.User;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class CustomizedUserRepositoryImpl implements  CustomizedUserRepository{
    @PersistenceContext
    private EntityManager em;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findByUsernameCustom(String username) { // 쿼리 작성
        QUser quser = QUser.user;
        JPAQuery<?> query = new JPAQuery<Void>(em);
        List<User> user = query.select(quser)
                .from(quser)
                .where(quser.username.contains(username))
                .fetch();
        return user;
    }

    @Override
    public List<User> findByUsernameJdbc(String username) {
        List<User> user = jdbcTemplate.query(
                "SELECT * FROM user where username like ?",
                new Object[]{"%"+username+"%"},
                new BeanPropertyRowMapper<>(User.class));
        return user;
    }
}
