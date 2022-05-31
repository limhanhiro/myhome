package com.example.thymeleafex.controller;

import com.example.thymeleafex.document.AutoComplete;
import com.example.thymeleafex.document.Elastic;
import com.example.thymeleafex.document.Popular;
import com.example.thymeleafex.document.SearchLog;
import com.example.thymeleafex.helper.EnToKo;
import com.example.thymeleafex.search.PopularKeywordDTO;
import com.example.thymeleafex.search.SearchRequestDTO;
import com.example.thymeleafex.service.ElasticService;
import org.apache.lucene.queryparser.classic.XQueryParser;
import org.apache.lucene.queryparser.flexible.precedence.processors.PrecedenceQueryNodeProcessorPipeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/elastic")
public class ElasticController {
    private final ElasticService service;

    @Autowired
    public ElasticController(ElasticService service){
        this.service = service;
    }

    @GetMapping("/search")
    public ModelAndView search(SearchRequestDTO dto, int page, String field,@RequestParam(required = false) String sort
                                                                           ,@RequestParam(required = false) String log) {
        //검색 조건 나누기
        String searchTerm = dto.getSearchTerm(); //

        //영어 변환
        String match1 = "[^\uAC00-\uD7A30-9a-zA-Z]"; // 영어 한글 숫자를 제외한 모든 문자
        String temp = searchTerm.replaceAll(match1," "); // 영어 한글 숫자를 제외한 모든 문자 공백으로 치환
        String[]temp1 = temp.split(" "); // 공백을 기준으로 배열로 만듬
        StringBuilder eng = new StringBuilder();
        for(int c=0; c < temp1.length;  c++){
            String check = EnToKo.tranEng(temp1[c]);
            if(c+1 == temp1.length){ // StringBuilder 마지막 부분 공백 넣지 않기 위한 조건문
                if(check.equals(temp1[c])){// 영어가아니라 똑같은 그대로 나옴 아닌 경우
                    eng.append(temp1[c]);
                }else{//영어를 한글로 바꿔서 가져온 경우
                    int vali = service.chkEng(check,dto); //바뀐 한글 값을 검색하여 갯수를 본다
                    if(vali >0){ // 하나이상 있는 경우 치환된 한글값으로 검색값 변경
                        eng.append(check);
                    }else { // 없는 경우 변환전 영어를 검색값으로 유지
                        eng.append(temp1[c]);
                    }
                }
            }else{ // StringBuilder 공백 넣는 부분
                if(check.equals(temp1[c])){
                    eng.append(temp1[c]+ " ");
                }else{
                    int vali = service.chkEng(check,dto);
                    if(vali >0){
                        eng.append(check+ " ");
                    }else {
                        eng.append(temp1[c]+ " ");
                    }
                }

            }
        }
        String last = eng.toString();// 영어로 변환한 문자들

        if (searchTerm.contains("-")) {
            String match = "[^\uAC00-\uD7A30-9a-zA-Z]";
            //String replace = searchTerm.replaceAll(match, " ");
            //String[] orginal = last.split(" "); //전체 단어를 자른다
            String[] strSplit = searchTerm.split("-"); // - 기준으로 자른다
            String[] strReplace = new String[strSplit.length];
            for (int i = 0; i < strSplit.length; i++) { // -기분으로 자른 배열에에 있는 특수 문자 전체 제거후 복사
                String replace1 = strSplit[i].replaceAll(match, " ");
                strReplace[i] = replace1;
            }
            StringBuilder must = new StringBuilder(); // must 쿼리 StringBuilder 생성
            StringBuilder mustNot = new StringBuilder();// mustNot 쿼리 StringBuilder 생성
            for (int i = 0; i < strReplace.length; i++) {
                String bool = null;
                if (i == 0) {
                    must.append(strReplace[0] + " "); // 첫번째 단어 must 쿼리로
                } else {
                    String[] strTemp = strReplace[i].split(" ");
                    // strReplace 한칸 String 에 있는 값을 공백을 기준으로 잘라서 배열을 만든다 strReplce의  0번째를 빼면 strReplace를 이용해 만든
                    // strTemp 배열은 0번째는 무조건 - 가 앞에 있던 단어
                    for (int k = 0; k < strTemp.length; k++) {
                        String boolNot = null;
                        if (k == 0) {
                            bool = strTemp[0];//- 가 앞에 있던 단어 boolQuery 처리
                        } else {
                            boolNot = strTemp[k]; // 나머진 다 일반 검색 처리
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
            String boolQuery = mustNot.toString(); // 공백을 기준으로 bool 쿼리 단어 String 으로
            String searchQuery = must.toString(); // 공백을 기준으로 검색 쿼리 단어 String 으로
            //searchquery 세팅
            //불용어 동의어 세팅
            String[] test = searchQuery.split(" ");
            StringBuilder first = new StringBuilder();
            for(int i=0; i<test.length;i++){
                dto.setSearchTerm(test[i]);
                String result = service.correctTrans(dto);
                System.out.println(result);
                if(i+1 == test.length){
                    if(result == null){ // 오타교정에 검색 결과 있으면 검색 결과를 넣는다
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
                String result = service.stopTrans(dto);
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
            //boolquery 세팅
            //불용어 동의어 세팅
            String[] test3 = boolQuery.split(" ");
            StringBuilder thrid = new StringBuilder();
            for(int i=0; i<test3.length;i++){
                dto.setSearchTerm(test[i]);
                String result = service.correctTrans(dto);
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
                String result = service.stopTrans(dto);
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
            dto.setSearchTerm(second.toString()); // 동의어 불용어 처리 완료한  searchTerm
            dto.setBoolTerm(four.toString()); // 동의어 불용어 처리 완료한  boolQuery

        }else{ // - 가 안들어 간 경우 동의어 불용어 처리
            //String match = "[^\uAC00-\uD7A30-9a-zA-Z]";
            //String replace = searchTerm.replaceAll(match, " ");
            String[] test = last.split(" ");
            StringBuilder first = new StringBuilder();
            for(int i=0; i<test.length;i++){
                dto.setSearchTerm(test[i]);
                String result = service.correctTrans(dto);
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
                String result = service.stopTrans(dto);
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

            //파라미터에 따라 검색 필드 나누기
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
            } else {// 최초 패이지 접속시 전체 정보 가져올 때 바로 model 리턴
                String field1 = "ALL";
                List<String> fields = new ArrayList<>();
                fields.add(field1);
                dto.setFields(fields);
                ModelAndView mvAll = new ModelAndView("/elastic/elasticList");
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
            ModelAndView mvSearch = new ModelAndView("/elastic/elasticList");
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
