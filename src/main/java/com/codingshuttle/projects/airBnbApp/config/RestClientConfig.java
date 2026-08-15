package com.codingshuttle.projects.airBnbApp.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @Qualifier(value = "holidayClient")
    RestClient restClient(){
        return RestClient.builder().build();
    }
}
