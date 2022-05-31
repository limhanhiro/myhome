package com.example.thymeleafex.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.Date;


@Data
@Document(indexName = "jeju")
public class  Elastic {

    private int row;
    private String ID;
    private String TITLE;
    private String CONTENTS;
    private String WRITER;
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    private Date created; // 등록시간순으로 sorting 하기위한 데이터

}
