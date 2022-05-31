package com.example.thymeleafex.document;


import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "crawling1")
public class Crawling1 {

    private int row;
    private String ID;
    private String TITLE;
    private String CONTENTS;
    private String WRITER;
}
