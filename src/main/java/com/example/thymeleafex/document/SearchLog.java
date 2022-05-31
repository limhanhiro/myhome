package com.example.thymeleafex.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Document(indexName = "iosearch-search-log-000001")
public class SearchLog {

    private List<String> query;
    private String domain;
    private String sort;
    private String category;
    private String createdDate; // 등록시간순으로 sorting 하기위한 데이터

}
