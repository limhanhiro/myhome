package com.example.thymeleafex.document;

import lombok.Data;

@Data
public class Popular {
    private int row;
    private String key;
    private int doc_count;
}
