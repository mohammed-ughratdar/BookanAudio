/*package com.bookanaudio;

import com.bookanaudio.s3.service.ExtractPagesService;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

@Component
public class SqsEventProcessor {

    @Autowired
    private ExtractPagesService extractPagesService;

    public void processEvent(SQSEvent sqsEvent) {
        sqsEvent.getRecords().forEach(record -> {
            System.out.println("Processing SQS message: " + record.getBody());

            try {
               
                Long bookId = Long.valueOf(record.getMessageAttributes().get("bookId").getStringValue());
                String fileName = record.getMessageAttributes().get("bookName").getStringValue();
                System.out.println("Parsed Book ID: " + bookId + ", File Name: " + fileName);

                extractPagesService.uploadBook(bookId, fileName);

            } catch (Exception e) {
                System.err.println("Error processing SQS message: " + e.getMessage());
            }
        });
    }
}
*/