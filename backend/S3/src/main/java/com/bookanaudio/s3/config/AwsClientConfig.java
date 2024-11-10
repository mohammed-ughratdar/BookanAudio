package com.bookanaudio.s3.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import com.amazonaws.regions.Regions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Configuration
public class AwsClientConfig {

    @Value("${iam_access_key}")
    private String awsAccessKeyId;

    @Value("${iam_secret_key}")
    private String awsSecretKey;

    @Value("${aws_region}")
    private String awsRegion;

    @Bean
    public AmazonSQS amazonSQS() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretKey);

        return AmazonSQSClientBuilder.standard()
                .withRegion(Regions.fromName(awsRegion))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}