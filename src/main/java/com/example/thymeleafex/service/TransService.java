package com.example.thymeleafex.service;

import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransService {

    private static final Logger Log = LoggerFactory.getLogger(ElasticService.class);

    private final RestHighLevelClient client;

    @Autowired
    public TransService(RestHighLevelClient client){
        this.client = client;
    }
}
