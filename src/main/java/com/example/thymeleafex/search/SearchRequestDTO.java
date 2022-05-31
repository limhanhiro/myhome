package com.example.thymeleafex.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.elasticsearch.search.sort.SortOrder;

import java.util.List;

@Data

public class SearchRequestDTO extends PageRequestDTO{
    private List<String> fields;
    private String searchTerm;
    private String boolTerm;
    private String sortBy;
    private SortOrder order;
    private List<String> word;
}
