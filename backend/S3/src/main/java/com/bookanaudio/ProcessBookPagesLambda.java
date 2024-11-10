package com.bookanaudio;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bookanaudio.s3.service.ExtractPagesService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Component
public class ProcessBookPagesLambda {

    private static final Logger log = LogManager.getLogger(ProcessBookPagesLambda.class);

    @Autowired
    private ExtractPagesService extractPagesService;

    public String processSQSEvent(SQSEvent event) {
        try {
            // Process each message in the SQS event
            for (SQSEvent.SQSMessage message : event.getRecords()) {
                log.info("Processing message from queue: {}", message.getBody());

                // Split the message body by comma and extract the values
                String[] messageBody = message.getBody().split(",");
                
                if (messageBody.length != 2) {
                    log.error("Invalid message format: {}", message.getBody());
                    continue;
                }

                try {
                    Long bookId = Long.parseLong(messageBody[0]);  // Extract bookId
                    String fileName = messageBody[1];  // Extract fileName
                    
                    log.info("Book ID: {}, File Name: {}", bookId, fileName);

                    // Call the ExtractPagesService to upload the book
                    extractPagesService.uploadBook(bookId, fileName);
                } catch (NumberFormatException e) {
                    log.error("Failed to parse bookId from message: {}", message.getBody(), e);
                } catch (Exception e) {
                    log.error("Error processing message: {}", message.getBody(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error processing SQS event: {}", e.getMessage(), e);
        }
        
        return null;  // No need to return anything for SQS event handling
    }
}
