package com.example.thymeleafex.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String password;
    private Boolean enabled;  // 인증시 활성화된 사용자인지 값을 같이 넘겨줘야 한다.

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))

    private List<Role> role = new ArrayList<>(); //권한과 mapping을 시켜 준다

    //@OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Board> board = new ArrayList<>();
}