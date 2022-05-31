package com.example.thymeleafex.controller;

import com.example.thymeleafex.document.AutoComplete;
import com.example.thymeleafex.document.Elastic;
import com.example.thymeleafex.document.Popular;
import com.example.thymeleafex.document.SearchLog;
import com.example.thymeleafex.search.PopularKeywordDTO;
import com.example.thymeleafex.search.SearchRequestDTO;
import com.example.thymeleafex.service.JejuMulService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/jejumul4")
public class JejuMul4Controller {
    private final JejuMulService service;
    private String lan1 = "CONTENTS.ko";
    private String lan2 = "CONTENTS.en";
    private String lan3 = "CONTENTS.ja";
    private String lan4 = "CONTENTS.de";
    private String lan5 = "CONTENTS.zh";
    @Autowired
    public JejuMul4Controller(JejuMulService service){
        this.service = service;
    }

    @GetMapping("/search")
    public ModelAndView search(SearchRequestDTO dto, int page, String field,@RequestParam(required = false) String sort
                                                                           ,@RequestParam(required = false) String log) {
            //파라미터에 따라 검색 필드 나누기
            if (field.equals("t")) {
                String field1 = "TITLE";
                List<String> fields = new ArrayList<>();
                fields.add(field1);
                dto.setFields(fields);
            } else if (field.equals("c")) {
                List<String> fields = new ArrayList<>();
                fields.add(lan1);
                fields.add(lan2);
                fields.add(lan3);
                fields.add(lan4);
                fields.add(lan5);
                dto.setFields(fields);
            } else if (field.equals("tc")) {
                String field1 = "TITLE";
                List<String> fields = new ArrayList<>();
                fields.add(field1);
                fields.add(lan1);
                fields.add(lan2);
                fields.add(lan3);
                fields.add(lan4);
                fields.add(lan5);
                dto.setFields(fields);
            } else {// 최초 패이지 접속시 전체 정보 가져올 때 바로 model 리턴
                String field1 = "ALL";
                List<String> fields = new ArrayList<>();
                fields.add(field1);
                dto.setFields(fields);
                ModelAndView mvAll = new ModelAndView("/elastic/jejumul4List");
                final int size = 10; // 한페이지에 몇개 보일지 결정
                dto.setSize(size);
                dto.setPage(page);
                List<Elastic> elastic = service.search(dto);
                int startPage = Math.max(1, page - 4);
                int endPage = page + 4;
                String total = "total"; // 화면에서 전체 검색인지 아닌지 보기 위한 model
                // 인기 검색어 가져 오기 위한 세팅
                PopularKeywordDTO pdto = new PopularKeywordDTO();
                String setField = "query";
                List<String> popularField = new ArrayList<>();
                popularField.add(setField);
                pdto.setFields(popularField);
                //Popular popular = new Popular();
                List<Popular> populars = service.popular(pdto);
                System.out.println(populars.size());
                mvAll.addObject("populars", populars);
                mvAll.addObject("elastic", elastic);
                mvAll.addObject("page", page);
                mvAll.addObject("startPage", startPage);
                mvAll.addObject("endPage", endPage);
                mvAll.addObject("total", total);
                return mvAll;

            }
            ModelAndView mvSearch = new ModelAndView("/elastic/jejumul4List");
            //파라미터 에 따라 정렬 조건 나누기
            if (sort.equals("rec")) {
                dto.setSortBy("CRAWL_DATE");
                mvSearch.addObject("sort", sort);
            } else {
                mvSearch.addObject("sort", "com");
            }

            int total = service.totalCount(dto).size(); //전체검색결과 개수 가져옴옴
           final int size = 10;
            dto.setSize(size);// 페이지에 따라 가져오기 위한 세팅
            dto.setPage(page);// 페이지에 따라 가져오기 위한 세팅
            List<Elastic> elastic = service.search(dto);
            //검색 로그 쌓기 "^^" 파라미터는 최초 접속시 로그 쌓는 거 막기 위해
            if (elastic.size() > 0 && log.equals("^^")) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime ymdhms = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), 0);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                SearchLog searchLog = new SearchLog();
                String[] inputQuery = dto.getSearchTerm().split(" ");
                //검색어 = "query"  로 잘라서 log 에 저장 한다
                List<String> query = new ArrayList<>();
                for (int k = 0; k < inputQuery.length; k++) {
                    query.add(inputQuery[k]);
                }
                searchLog.setQuery(query);
                searchLog.setSort(sort);
                searchLog.setCategory(field);
                searchLog.setCreatedDate(ymdhms.format(formatter));
                searchLog.setDomain("jeju");
                Boolean result = service.putSearchLog(searchLog);
                if (result) {
                    System.out.println("로그 쌓기 성공");
                } else {
                    System.out.println("로그 쌓기 실패");
                }
            }
//        인기 검색어 가져오기
            PopularKeywordDTO pdto = new PopularKeywordDTO();
            String setField = "query";
            List<String> popularField = new ArrayList<>();
            popularField.add(setField);
            pdto.setFields(popularField);
            //Popular popular = new Popular();
            List<Popular> populars = service.popular(pdto);
            System.out.println(populars.size());
            int endPage;
            int startPage;
            if (total < size) {
                startPage = 1;
                endPage = 1;
            } else if (total % size == 0) {
                startPage = Math.max(1, page - 4);
                endPage = Math.min(page + 4, (total / size));
            } else {
                startPage = Math.max(1, page - 4);
                endPage = Math.min(page + 4, (total / size + 1));
            }
            mvSearch.addObject("elastic", elastic);
            mvSearch.addObject("page", page);
            mvSearch.addObject("startPage", startPage);
            mvSearch.addObject("endPage", endPage);
            mvSearch.addObject("total", total);
            mvSearch.addObject("field", field);
            mvSearch.addObject("populars", populars);
            return mvSearch;
        }

    @GetMapping("/elasticForm")
    public ModelAndView getById(String ID){
        //아이디 값으로 세부 정보
        ModelAndView mv = new ModelAndView("/elastic/elasticForm");
        Elastic elastic = service.getById(ID);
        mv.addObject("elastic",elastic);
        return mv;

    }
    @PostMapping("/autoComplete")
    @ResponseBody
    public List<AutoComplete> autoComplete(@ModelAttribute SearchRequestDTO dto){
        //검색어 자동 완성
        System.out.println("autoComplete Controller");
        return service.autoComplete(dto);
    }
    @GetMapping("/elasticList")
    public ModelAndView All(SearchRequestDTO dto, int page){
        ModelAndView mv = new ModelAndView("/elastic/elasticList");
        final int size = 10; // 한페이지에 몇개 보일지 결정
        dto.setSize(size);
        dto.setPage(page);

        List<Elastic> elastic = service.All(dto);
        int startPage = Math.max(1,page-4);
        int endPage = page+4;
        String total = "all";
        mv.addObject("elastic",elastic);
        mv.addObject("page",page);
        mv.addObject("startPage",startPage);
        mv.addObject("endPage",endPage);
        mv.addObject("total",total);

        return mv;
    }
}
