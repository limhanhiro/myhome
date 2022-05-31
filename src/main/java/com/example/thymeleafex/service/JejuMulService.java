package com.example.thymeleafex.service;

import com.example.thymeleafex.document.*;
import com.example.thymeleafex.helper.Indices;
import com.example.thymeleafex.search.PopularKeywordDTO;
import com.example.thymeleafex.search.SearchRequestDTO;
import com.example.thymeleafex.search.utils.AggregationUtil;
import com.example.thymeleafex.search.utils.SearchUtil;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class JejuMulService {
    //rivate static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger Log = LoggerFactory.getLogger(JejuMulService.class);

    private final RestHighLevelClient client;

    @Autowired
    public JejuMulService(RestHighLevelClient client){
        this.client = client;
    }


    private List<Elastic> searchInternal(final SearchRequest request) {
        if(request == null){
            Log.error("Fail to build search request");
            return Collections.emptyList();
        }
        try{
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits searchHits = response.getHits();
            List<Elastic> elastics = new ArrayList<>();
            int row = 1;
            for(SearchHit hit : searchHits){
                Map<String,Object> resultMap= hit.getSourceAsMap();
                Elastic elastic = new Elastic();
                elastic.setRow(row);
                elastic.setCONTENTS((String) resultMap.get("CONTENTS.default"));
                elastic.setTITLE((String) resultMap.get("TITLE"));
                elastic.setWRITER((String) resultMap.get("WRITER"));
                elastic.setID((String) resultMap.get("ID"));
                elastics.add(elastic);
                row++;
            }
            return elastics;
        }catch (Exception e){
            Log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    public List<Elastic> search(final SearchRequestDTO dto){
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.JEJUMUL4_INDEX,
                dto
        );
        return searchInternal(request);
    }
    public List<Elastic> totalCount(final SearchRequestDTO dto){
        dto.setPage(0);
        dto.setSize(10000);
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.JEJUMUL4_INDEX,
                dto
        );
        return searchInternal(request);
    }

    public Elastic getById(final String ID){
        try {
            final GetResponse documentFields = client.get(
                new GetRequest(Indices.JEJUMUL4_INDEX,ID),
                RequestOptions.DEFAULT
            );
            if(documentFields == null || documentFields.isSourceEmpty()){
                return null;
            }
            Map<String,Object> resultMap= documentFields.getSourceAsMap();
            Elastic elastic = new Elastic();
            elastic.setCONTENTS((String) resultMap.get("CONTENTS.default"));
            elastic.setTITLE((String) resultMap.get("TITLE"));
            elastic.setWRITER((String) resultMap.get("WRITER"));
            elastic.setID((String) resultMap.get("ID"));
            return elastic;
        }catch (final Exception e){
            Log.error(e.getMessage(),e);
            return null;
        }
    }


    public List<Elastic> All(SearchRequestDTO dto) {
        final SearchRequest request = SearchUtil.buildMatchAllRequest(
                Indices.JEJUMUL4_INDEX,
                dto
        );
        return searchInternal(request);

    }
    public Boolean putSearchLog(final SearchLog searchLog){

        for(int i =0; i<searchLog.getQuery().size(); i++){
            IndexRequest request = new IndexRequest(Indices.SEARCH_INDEX);
            String jsonString = "{"+
                    "\"category\":\""+searchLog.getCategory()+"\"," +
                    "\"query\":\""+searchLog.getQuery().get(i)+"\","+
                    "\"sort\":\""+searchLog.getSort()+"\"," +
                    "\"createdDate\":\""+searchLog.getCreatedDate()+"\","+
                    "\"domain\":\""+searchLog.getDomain()+"\""+
                    "}";
            request.source(jsonString, XContentType.JSON);
            IndexResponse indexResponse;
            try{
                indexResponse = client.index(request,RequestOptions.DEFAULT);
//                if(i+1 == searchLog.getQuery().size()){
//                    return true;
//                }else {
//                    continue;
//                }
            }catch (IOException e){
                Log.error(e.getMessage(),e);
                return false;
            }
        }
        return true;

    }

    public List<Popular> popular(PopularKeywordDTO pdto) {
        final SearchRequest request = AggregationUtil.buildAggregationRequest(
                Indices.SEARCH_INDEX,
                pdto
        );
        if(request == null){
            Log.error("Fail to build aggregation request");
            return Collections.emptyList();
        }
        try{
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Terms terms = response.getAggregations().get("group_by_query");
            List<Popular> populars = new ArrayList<>();
            int row = 1;
            for(Terms.Bucket bucket1 : terms.getBuckets()){
                Popular popular = new Popular();
                popular.setKey(bucket1.getKeyAsString());
                popular.setDoc_count((int)bucket1.getDocCount());
                popular.setRow(row);
                populars.add(popular);
                row++;
            }
            return populars;
        }catch (Exception e){
            Log.error(e.getMessage(), e);
            return Collections.emptyList();
        }

    }

    public List<AutoComplete> autoComplete(SearchRequestDTO dto){
        System.out.println("autoComplete Service");
        List<String> fields = new ArrayList<>();
        fields.add("keyword");
        dto.setFields(fields);
        dto.setPage(0);
        dto.setSize(10);
        System.out.println(dto.getSearchTerm());
        System.out.println(dto.getPage());
        System.out.println(dto.getSize());
        System.out.println(dto.getFields().get(0));
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.AUTO_INDEX,
                dto
        );
        if(request == null){
            Log.error("Fail to build search request");
            return Collections.emptyList();
        }
        try{
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits searchHits = response.getHits();
            List<AutoComplete> autoCompletes = new ArrayList<>();
            for(SearchHit hit : searchHits){
                Map<String,Object> resultMap= hit.getSourceAsMap();
                AutoComplete autoComplete = new AutoComplete();
                autoComplete.setKeyword((String) resultMap.get("keyword"));
                autoCompletes.add(autoComplete);
            }
            return autoCompletes;
        }catch (Exception e){
            Log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

}
