package com.example.thymeleafex.controller;

import com.example.thymeleafex.model.Board;
import com.example.thymeleafex.repository.BoardRepository;
import com.example.thymeleafex.service.BoardService;
import com.example.thymeleafex.vaildator.BoardVaildator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardVaildator boardVaildator;

    @Autowired
    private BoardService boardService;


    @GetMapping("/list")
    public String list(Model model, @PageableDefault(size = 2) Pageable pageable, @RequestParam(required = false, defaultValue = "") String searchText) { //pageable을 매개변수로 받기 때문에 url에 파라미터를 넣어도 된다   @PageableDefault 기본값 지정
        //Page<Board> board = boardRepository.findAll(pageable);//pageRequest 로 Page<> 리턴을 받는다
        Page<Board> board = boardRepository.findByTitleContainingOrContentContaining(searchText,searchText,pageable);//pageRequest 로 Page<> 리턴을 받는다
        int startPage = Math.max(1,board.getPageable().getPageNumber() - 4); //최대값은 0 전체 페이지수 -4
        int endPage = Math.min(board.getPageable().getPageNumber() + 4,board.getTotalPages()); //최소값은 전체 페이지수 전체페이지수 +4
        model.addAttribute("board",board);//model 에 담아서 전송
        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);
        return "board/list";
    }
    @GetMapping("/form")
    public String form(Model model, @RequestParam(required = false) Long id){ //@RequestParam(required = false) 파라미터가 필수인지 아닌지 결정
        if (id == null) {
            model.addAttribute("board",new Board());//form 이 있는 문서 들어갈때 board 이름의 model을 생성
        }else{
            Board board = boardRepository.findById(id).orElse(null); // key 값으로 값을 찾아서 가져오고 없을 경우 nulll 을 가져온다
            model.addAttribute("board",board);
        }
        return "board/form";
    }

    @PostMapping("/form")
    public String formSubmit(@Valid Board board, BindingResult bindingResult, Authentication authentication){ //같은이름의 getmapping URL 에서 담은 model을 가져옴
        boardVaildator.validate(board,bindingResult);
        if (bindingResult.hasErrors()){
            //System.out.println("글자수 미달");
            return "board/form";
        }
        //Authentication a = SecurityContextHolder.getContext().getAuthentication(); 사용자 정보를 담아오는 다른 방법
        String username = authentication.getName();//스프링 시큐리티로 사용자 정보를 가져온다
        System.out.println(username);
        //System.out.println("글자수 안미달");
        boardService.save(username,board);
        //boardRepository.save(board); // 담아온 model을 이용하여 insert(save) key 값을 맞춰주면 알아서 update 해준다
        return "redirect:/board/list";//다시한번 조회가 되면서 이동 
    }
}
