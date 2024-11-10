package com.bookanaudio.books.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SQSService {

    private final AmazonSQS amazonSQS;
    private final String sqsQueueUrl;

    public SQSService(
            @Value("${iam_access_key}") String awsAccessKeyId,
            @Value("${iam_secret_key}") String awsSecretKey,
            @Value("${aws_region}") String awsRegion,
            @Value("${sqs_url}") String sqsQueueUrl
    ) {
        this.sqsQueueUrl = sqsQueueUrl;

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretKey);
        this.amazonSQS = AmazonSQSClientBuilder.standard()
                .withRegion(Regions.fromName(awsRegion))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    public void sendMessageToSQS(Long bookId, String fileName) {
        String messageBody = String.format("{\"bookId\": \"%d\", \"bookName\": \"%s\"}", bookId, fileName);

        SendMessageRequest sendMessageRequest = new SendMessageRequest()
            .withQueueUrl(sqsQueueUrl)
            .withMessageBody(messageBody)
            .withDelaySeconds(0); 

        amazonSQS.sendMessage(sendMessageRequest);
    }
}
