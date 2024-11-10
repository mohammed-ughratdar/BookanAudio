package com.bookanaudio;

import com.bookanaudio.s3.config.AppConfig;
import com.bookanaudio.s3.config.AwsClientConfig;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.bookanaudio.s3.service.ExtractPagesService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component("awsLambdaSqsFunction")
public class AwsLambdaSqsFunction implements Function<Void, Void> {

    private static final Logger log = LoggerFactory.getLogger(AwsLambdaSqsFunction.class);

    @Autowired
    private ExtractPagesService extractPagesService;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private AmazonSQS amazonSqs;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Void apply(Void empty) {
        String queueUrl = appConfig.getQueueUrl();

        log.info("Retrieving messages from queue: {}", queueUrl);

        List<Message> messageList = getQueueMessageByQueueUrl(queueUrl);

        for (Message message : messageList) {
            log.info("Received message: {}", message.getBody());

            try {
                JsonNode messageJson = objectMapper.readTree(message.getBody());
                Long bookId = messageJson.get("bookId").asLong();
                String fileName = messageJson.get("bookName").asText();

                log.info("Parsed Book ID: {}, File Name: {}", bookId, fileName);

                extractPagesService.uploadBook(bookId, fileName);

            } catch (Exception e) {
                log.error("Error processing message: {}", message.getBody(), e);
            }
        }

        return null;
    }

    private List<Message> getQueueMessageByQueueUrl(String queueUrl) {
        log.info("Getting messages from queue URL: {}", queueUrl);

        ReceiveMessageRequest messageRequest = new ReceiveMessageRequest(queueUrl)
                .withWaitTimeSeconds(5)
                .withMaxNumberOfMessages(2);

        List<Message> messages = amazonSqs.receiveMessage(messageRequest).getMessages();

        log.info("Received {} messages from queue", messages.size());

        return messages;
    }
}
