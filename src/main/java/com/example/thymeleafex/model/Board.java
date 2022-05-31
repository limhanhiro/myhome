package com.example.thymeleafex.model;



import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @Size(min=2, max=30,message = "제목은 2자 이상 30자 이하")
    private String title;
    private String content;

    @ManyToOne
    @JoinColumn(name= "user_id")//,referencedColumnName = "id" 프라이머리키에 경우 없어도 됨
    @JsonIgnore
    private User user;
}
