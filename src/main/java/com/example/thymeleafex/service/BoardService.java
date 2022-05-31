package com.example.thymeleafex.service;

import com.example.thymeleafex.model.Board;
import com.example.thymeleafex.model.User;
import com.example.thymeleafex.repository.BoardRepository;
import com.example.thymeleafex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    public Board save(String username, Board board){
        User user = userRepository.findByUsername(username);
        board.setUser(user);
        return boardRepository.save(board);
    }
}
