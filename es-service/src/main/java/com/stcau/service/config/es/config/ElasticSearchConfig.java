package com.stcau.service.config.es.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
@Configuration
public class ElasticSearchConfig {
    @Value("${es.addr}")
    private String https;
    @Value("${es.userName}")
    private String userName;
    @Value("${es.password}")
    private String password;

    @Bean(value = "restClient")
    public RestHighLevelClient getTransportClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));

        String[] httpList = https.split(",");
        List<HttpHost> list = new ArrayList<HttpHost>();
        for (String http : httpList) {
            String[] url = http.split(":");
            list.add(new HttpHost(url[0], Integer.parseInt(url[1]), "http"));
        }
        HttpHost[] httpHosts = new HttpHost[list.size()];
        list.toArray(httpHosts);
        RestClientBuilder builder = RestClient.builder(httpHosts)
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });
        return new RestHighLevelClient(builder);
    }
}
