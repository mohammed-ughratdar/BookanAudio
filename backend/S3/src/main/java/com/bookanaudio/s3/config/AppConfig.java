package com.bookanaudio.s3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("${sqs_url}")
    private String queueUrl;

    public String getQueueUrl(){
        return queueUrl;
    }

}