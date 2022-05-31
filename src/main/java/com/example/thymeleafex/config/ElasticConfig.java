package com.example.thymeleafex.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.thymeleafex.repository")
@ComponentScan(basePackages = {"com.example.thymeleafex"})
public class ElasticConfig extends AbstractElasticsearchConfiguration {

    @Value("${elastic.host}")
    private String host;

    @Value("${elastic.port}")
    private String port;

    @Value("${elastic.username}")
    private String username;

    @Value("${elastic.password}")
    private String password;

    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration.MaybeSecureClientConfigurationBuilder builder =
                (ClientConfiguration.MaybeSecureClientConfigurationBuilder) ClientConfiguration.builder()
                        .connectedTo(host+":"+port)
                        .withBasicAuth(username,password);
        final ClientConfiguration clientConfiguration = builder.build();
        return RestClients.create(clientConfiguration).rest();
    }
}
