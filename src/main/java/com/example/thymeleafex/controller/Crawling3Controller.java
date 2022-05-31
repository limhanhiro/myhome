package com.example.thymeleafex.controller;

import com.example.thymeleafex.document.*;
import com.example.thymeleafex.helper.EnToKo;
import com.example.thymeleafex.search.PopularKeywordDTO;
import com.example.thymeleafex.search.SearchRequestDTO;
import com.example.thymeleafex.service.Crawling3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/crawling3")
public class Crawling3Controller {
    private final Crawling3Service service;

    @Autowired
    public Crawling3Controller(Crawling3Service service){
        this.service = service;
    }

    @GetMapping("/search")
    public ModelAndView search(SearchRequestDTO dto, int page, String field,@RequestParam(required = false) String sort
                                                                           ,@RequestParam(required = false) String log) {
        //검색 조건 나누기
        String searchTerm = dto.getSearchTerm(); //

        //영어 변환
        String match1 = "[^\uAC00-\uD7A30-9a-zA-Z]";
        String temp = searchTerm.replaceAll(match1," ");
        String[]temp1 = temp.split(" ");
        StringBuilder eng = new StringBuilder();
        for(int c=0; c < temp1.length;  c++){
            String check = EnToKo.tranEng(temp1[c]);
            if(c+1 == temp1.length){
                if(check.equals(temp1[c])){
                    eng.append(temp1[c]);//변환 하지 않고 나온 경우
                }else{
                    int vali = service.crawling3chkEng(check,dto);//변환 한 값이 의미 있는 검생어 인진 check
                    if(vali >0){
                        eng.append(check); // 검색결과 한개라도 있으면 변환한 단어
                    }else {
                        eng.append(temp1[c]); // 검색결과 없으면 변환 전 단어
                    }
                }
            }else{// for문 마지막 공백 처리를 위한 조건문
                if(check.equals(temp1[c])){
                    eng.append(temp1[c]+ " ");
                }else{
                    int vali = service.crawling3chkEng(check,dto);
                    if(vali >0){
                        eng.append(check+ " ");
                    }else {
                        eng.append(temp1[c]+ " ");
                    }
                }

            }
        }
        String last = eng.toString();
        //StringBuilder eng 를 String 으로 변환환
        if (searchTerm.contains("-")) {
            String match = "[^\uAC00-\uD7A30-9a-zA-Z]";
            //String replace = searchTerm.replaceAll(match, " ");
            String[] orginal = last.split(" "); //전체 단어를 자른다
            String[] strSplit = searchTerm.split("-"); // - 기준으로 자른다
            String[] strReplace = new String[strSplit.length]; // -기준으로 자른 배열에에 있는 특수 문자 전체 제거
            for (int i = 0; i < strSplit.length; i++) {
                String replace1 = strSplit[i].replaceAll(match, " ");
                strReplace[i] = replace1;
            }
            StringBuilder must = new StringBuilder();
            StringBuilder mustNot = new StringBuilder();
            for (int i = 0; i < strReplace.length; i++) {
                String bool = null;
                if (i == 0) {
                    must.append(strReplace[0] + " ");
                } else {
                    String[] strTemp = strReplace[i].split(" ");
                    for (int k = 0; k < strTemp.length; k++) {
                        String boolNot = null;
                        if (k == 0) {
                            bool = strTemp[0];
                        } else {
                            boolNot = strTemp[k];
                            if (i == strReplace.length - 1) {
                                must.append(boolNot);
                            } else {
                                must.append(boolNot + " ");
                            }

                        }
                    }
                    if (i == strReplace.length - 1) {
                        mustNot.append(bool);
                    } else {
                        mustNot.append(bool + " ");
                    }
                }
            }
            String boolQuery = mustNot.toString();
            String searchQuery = must.toString();
            //searchquey 세팅
            String[] test = searchQuery.split(" ");
            StringBuilder first = new StringBuilder();
            for(int i=0; i<test.length;i++){
                dto.setSearchTerm(test[i]);
                String result = service.crawling3correctTrans(dto);
                System.out.println(result);
                if(i+1 == test.length){
                    if(result == null){
                        first.append(test[i]);
                    }else{
                        first.append(result);
                    }
                }else {
                    if(result == null){
                        first.append(test[i]+ " ");
                    }else{
                        first.append(result+ " ");
                    }
                }
            }
            String first1 = first.toString();
            String[] test1 = first1.split(" ");
            StringBuilder second = new StringBuilder();
            for(int j=0; j < test1.length; j++){
                dto.setSearchTerm(test1[j]);
                String result = service.crawling3stopTrans(dto);
                if(j+1 == test1.length){
                    if(result == null){
                        second.append(test1[j]);
                    }else {
                        continue;
                    }
                }else {
                    if(result == null){
                        second.append(test1[j]+" ");
                    }else {
                        continue;
                    }
                }
            }


            String[] test3 = boolQuery.split(" ");
            StringBuilder thrid = new StringBuilder();
            for(int i=0; i<test3.length;i++){
                dto.setSearchTerm(test[i]);
                String result = service.crawling3correctTrans(dto);
                System.out.println(result);
                if(i+1 == test3.length){
                    if(result == null){
                        thrid.append(test3[i]);
                    }else{
                        thrid.append(result);
                    }
                }else {
                    if(result == null){
                        thrid.append(test3[i]+ " ");
                    }else{
                        thrid.append(result+ " ");
                    }
                }
            }
            String thrid1 = thrid.toString();
            String[] test4 = thrid1.split(" ");
            StringBuilder four = new StringBuilder();
            for(int j=0; j < test4.length; j++){
                dto.setSearchTerm(test4[j]);
                String result = service.crawling3stopTrans(dto);
                if(j+1 == test4.length){
                    if(result == null){
                        four.append(test4[j]);
                    }else {
                        continue;
                    }
                }else {
                    if(result == null){
                        four.append(test4[j]+" ");
                    }else {
                        continue;
                    }
                }
            }
            dto.setSearchTerm(second.toString());
            dto.setBoolTerm(four.toString());
            
            //동의어 불용어 처리

        }else{ // - 가 안들어 간 경우 동의어 불용어 처리
            //String match = "[^\uAC00-\uD7A30-9a-zA-Z]";
            //String replace = searchTerm.replaceAll(match, " ");
            String[] test = last.split(" ");
            StringBuilder first = new StringBuilder();
            for(int i=0; i<test.length;i++){
                dto.setSearchTerm(test[i]);
                String result = service.crawling3correctTrans(dto);
                System.out.println(result);
                if(i+1 == test.length){
                    if(result == null){
                        first.append(test[i]);
                    }else{
                        first.append(result);
                    }
                }else {
                    if(result == null){
                        first.append(test[i]+ " ");
                    }else{
                        first.append(result+ " ");
                    }
                }
            }
            String first1 = first.toString();
            String[] test1 = first1.split(" ");
            StringBuilder second = new StringBuilder();
            for(int j=0; j < test1.length; j++){
                dto.setSearchTerm(test1[j]);
                String result = service.crawling3stopTrans(dto);
                if(j+1 == test1.length){
                    if(result == null){
                        second.append(test1[j]);
                    }else {
                        continue;
                    }
                }else {
                    if(result == null){
                        second.append(test1[j]+" ");
                    }else {
                        continue;
                    }
                }
            }
            dto.setSearchTerm(second.toString());
            System.out.println("최종검색어"+dto.getSearchTerm());
        }

            //검색 필드 나누기
            if (field.equals("t")) {
                String field1 = "TITLE";
                List<String> fields = new ArrayList<>();
                fields.add(field1);
                dto.setFields(fields);
            } else if (field.equals("c")) {
                String field1 = "CONTENTS";
                List<String> fields = new ArrayList<>();
                fields.add(field1);
                dto.setFields(fields);
            } else if (field.equals("tc")) {
                String field1 = "CONTENTS";
                String field2 = "TITLE";
                List<String> fields = new ArrayList<>();
                fields.add(field1);
                fields.add(field2);
                dto.setFields(fields);
            } else {
                String field1 = "ALL";
                List<String> fields = new ArrayList<>();
                fields.add(field1);
                dto.setFields(fields);
                ModelAndView mvAll = new ModelAndView("/elastic/crawling3List");
                final int size = 10; // 한페이지에 몇개 보일지 결정
                dto.setSize(size);
                dto.setPage(page);

                List<Crawling3> crawling3 = service.crawling3search(dto);
                int startPage = Math.max(1, page - 4);
                int endPage = page + 4;
                String total = "total";
                PopularKeywordDTO pdto = new PopularKeywordDTO();
                String setField = "query";
                List<String> popularField = new ArrayList<>();
                popularField.add(setField);
                pdto.setFields(popularField);
                //Popular popular = new Popular();
                List<Popular> populars = service.crawling3popular(pdto);
                System.out.println(populars.size());
                mvAll.addObject("populars", populars);
                mvAll.addObject("crawling3", crawling3);
                mvAll.addObject("page", page);
                mvAll.addObject("startPage", startPage);
                mvAll.addObject("endPage", endPage);
                mvAll.addObject("total", total);
                return mvAll;

            }
            ModelAndView mvSearch = new ModelAndView("/elastic/crawling3List");
            //정렬 조건 나누기
            if (sort.equals("rec")) {
                dto.setSortBy("CRAWL_DATE");
                mvSearch.addObject("sort", sort);
            } else {
                mvSearch.addObject("sort", "com");
            }

            int total = service.crawling3totalCount(dto).size();
            final int size = 10;
            dto.setSize(size);
            dto.setPage(page);
            List<Crawling3> crawling3 = service.crawling3search(dto);
            //검색 로그 쌓기
            if (crawling3.size() > 0 && log.equals("^^")) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime ymdhms = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), 0);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                SearchLog searchLog = new SearchLog();
                String[] inputQuery = dto.getSearchTerm().split(" ");
                List<String> query = new ArrayList<>();
                for (int k = 0; k < inputQuery.length; k++) {
                    query.add(inputQuery[k]);
                }
                searchLog.setQuery(query);
                searchLog.setSort(sort);
                searchLog.setCategory(field);
                searchLog.setCreatedDate(ymdhms.format(formatter));
                searchLog.setDomain("crawling3");
                Boolean result = service.crawling3putSearchLog(searchLog);
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
            List<Popular> populars = service.crawling3popular(pdto);
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
            mvSearch.addObject("crawling3", crawling3);
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
        ModelAndView mv = new ModelAndView("/elastic/crawling3Form");
        Crawling3 crawling3 = service.crawling3getById(ID);
        mv.addObject("crawling3",crawling3);
        return mv;

    }
    @PostMapping("/autoComplete")
    @ResponseBody
    public List<AutoComplete> autoComplete(@ModelAttribute SearchRequestDTO dto){
        System.out.println("autoComplete Controller");
        return service.crawling3autoComplete(dto);
    }
    @GetMapping("/elasticList")
    public ModelAndView All(SearchRequestDTO dto, int page){
        ModelAndView mv = new ModelAndView("/elastic/crawling3Form");
        final int size = 10; // 한페이지에 몇개 보일지 결정
        dto.setSize(size);
        dto.setPage(page);

        List<Crawling3> crawling3 = service.crawling3All(dto);
        int startPage = Math.max(1,page-4);
        int endPage = page+4;
        String total = "all";
        mv.addObject("crawling3",crawling3);
        mv.addObject("page",page);
        mv.addObject("startPage",startPage);
        mv.addObject("endPage",endPage);
        mv.addObject("total",total);

        return mv;
    }
}
