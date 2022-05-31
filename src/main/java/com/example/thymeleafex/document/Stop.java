package com.example.thymeleafex.document;

import com.example.thymeleafex.helper.Indices;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = Indices.STOP_INDEX)
public class Stop {
    private String keyword;
}
