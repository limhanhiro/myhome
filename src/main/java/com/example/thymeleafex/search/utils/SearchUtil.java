package com.example.thymeleafex.search.utils;

import com.example.thymeleafex.search.SearchRequestDTO;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

public class SearchUtil {
    private SearchUtil(final SearchRequestDTO dto){}

    public static QueryBuilder getQueryBuilder(final SearchRequestDTO dto) {
        if(dto == null){
            return null;
        }
        final List<String> fields = dto.getFields();
        if(CollectionUtils.isEmpty(fields)){
            return null;
        }
        if(fields.get(0).equals("ALL")){
            MatchAllQueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
            return queryBuilder;
        }
        if(fields.size() > 1) {
            MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(dto.getSearchTerm())
                    .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
                    .slop(5);
            fields.forEach(queryBuilder::field);

            return queryBuilder;
        }else {
            MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(dto.getSearchTerm())
                    .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
                    .slop(5);
            fields.forEach(queryBuilder::field);

            return queryBuilder;
        }
//        return fields.stream()
//                .findFirst()
//                .map(field ->
//                        QueryBuilders.matchQuery(field,dto.getSearchTerm())
//                                .operator(Operator.AND))
//                .orElse(null);
    }
    public static QueryBuilder getBoolQueryBuilder(final SearchRequestDTO dto) {
        if(dto == null){
            return null;
        }
        final List<String> fields = dto.getFields();
        if(CollectionUtils.isEmpty(fields)){
            return null;
        }
        if(fields.get(0).equals("ALL")){
            MatchAllQueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
            return queryBuilder;
        }
        if(fields.size() > 1) {
            MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(dto.getBoolTerm())
                    .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
                    .slop(5);

            fields.forEach(queryBuilder::field);

            return queryBuilder;
        }
        return fields.stream()
                .findFirst()
                .map(field ->
                        QueryBuilders.matchQuery(field,dto.getBoolTerm())
                                .operator(Operator.AND))
                .orElse(null);
    }
    public static QueryBuilder getWordQueryBuilder(final SearchRequestDTO dto){
        if(dto == null){
            return null;
        }
        final List<String> words = dto.getWord();
        return words.stream()
                .findFirst()
                .map(field ->
                        QueryBuilders.matchQuery(field,dto.getSearchTerm())
                                .operator(Operator.AND))
                .orElse(null);
    }

    public static QueryBuilder getQueryBuilder(final String field, final Date date){
        return QueryBuilders.rangeQuery(field).gte(date);
    }

    public static SearchRequest buildSearchRequest(final String indexName, final SearchRequestDTO dto){
        try {
            SearchSourceBuilder builder = new SearchSourceBuilder();
                final int page = dto.getPage();
                final int size = dto.getSize();
                final int from = page <= 0 ? 0 : page * size;
            if(dto.getBoolTerm() != null){
                final QueryBuilder mustQuery = getQueryBuilder(dto);
                final QueryBuilder mustNotQuery = getBoolQueryBuilder(dto);
                final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                        .mustNot(mustNotQuery)
                        .must(mustQuery);
                builder
                        .from(from)
                        .size(size)
                        .query(boolQuery);
            }else {
                builder
                        .from(from)
                        .size(size)
                        .query(getQueryBuilder(dto));;

            }
            if(dto.getSortBy() != null){
                builder = builder.sort(
                        dto.getSortBy(),
                        dto.getOrder() != null ? dto.getOrder() : SortOrder.ASC
                );
            }

            final SearchRequest request = new SearchRequest(indexName);
            System.out.println(builder.toString());
            request.source(builder);
            return request;

        } catch (final Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static SearchRequest buildAllSearchRequest(final String indexName, final SearchRequestDTO dto){
        try{
            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .query(getQueryBuilder(dto));

        final SearchRequest request = new SearchRequest(indexName);
        request.source(builder);

        return request;
        }catch (final Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static SearchRequest  buildMatchAllRequest(final String indexName,final SearchRequestDTO dto){
        try {
            final int page = dto.getPage();
            final int size = dto.getSize();
            final int from = page <= 0 ? 0 : page * size;
            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .from(from)
                    .size(size)
                    .query(getQueryBuilder(dto));
            if(dto.getSortBy() != null){
                builder = builder.sort(
                        dto.getSortBy(),
                        dto.getOrder() != null ? dto.getOrder() : SortOrder.ASC
                );
            }
            final SearchRequest request = new SearchRequest(indexName);
            request.source(builder);
            return request;
        } catch (final Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public static SearchRequest wordSearchRequest(String indexName, SearchRequestDTO dto) {
        try{
            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .query(getWordQueryBuilder(dto));

            final SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        }catch (final Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
