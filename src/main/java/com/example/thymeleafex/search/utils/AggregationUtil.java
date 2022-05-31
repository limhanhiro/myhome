package com.example.thymeleafex.search.utils;

import com.example.thymeleafex.search.PopularKeywordDTO;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.support.AggregationContext;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Map;

public class AggregationUtil {
    private AggregationUtil(final PopularKeywordDTO pdto){}

    public static SearchRequest buildAggregationRequest(final String indexName, final PopularKeywordDTO pdto){
        try {
            SearchSourceBuilder builder  = new SearchSourceBuilder();
            TermsAggregationBuilder aggregationBuilder= AggregationBuilders.terms("group_by_query").field("query");
            builder = builder.aggregation(aggregationBuilder).size(0);
            final SearchRequest request = new SearchRequest(indexName);
            System.out.println("인기 검색어"+builder.toString());
            request.source(builder);
            return request;

        }catch (final Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
