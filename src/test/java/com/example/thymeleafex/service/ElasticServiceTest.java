package com.example.thymeleafex.service;

import com.example.thymeleafex.document.Elastic;
import com.example.thymeleafex.search.SearchRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class ElasticServiceTest {
    @Autowired
    ElasticService es;
    @Test
    void 검색테스트(){
        SearchRequestDTO dto=new SearchRequestDTO();
        dto.setPage(0);
        List<String> filds = new ArrayList<>();
        filds.add("TITLE");
        filds.add("CONTENTS");
        dto.setFields(filds);
        dto.setSearchTerm("월라봉");
       List<Elastic> elastics= es.search(dto);
//        for(Elastic elastic : elastics)
//        {
//
//            System.out.println(elastic.getTITLE());
//        }
    }
}