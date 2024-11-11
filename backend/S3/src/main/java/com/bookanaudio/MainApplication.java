package com.bookanaudio;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.function.adapter.aws.FunctionInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import com.bookanaudio.s3.service.ExtractPagesService;
import java.util.function.Function;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@SpringBootApplication
public class MainApplication {

    private final ExtractPagesService extractPagesService;

    @Autowired
    public MainApplication(ExtractPagesService extractPagesService) {
        this.extractPagesService = extractPagesService;
    }

    public static void main(String[] args) {
        //SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public Function<SQSEvent, Void> processSqsEvent() {
		return sqsEvent -> {
			sqsEvent.getRecords().forEach(record -> {
				System.out.println("Processing SQS message: " + record.getBody());

				try {
					ObjectMapper mapper = new ObjectMapper();
					Map<String, String> data = mapper.readValue(record.getBody(), new TypeReference<Map<String, String>>() {});

					Long bookId = Long.valueOf(data.get("bookId"));
					String fileName = data.get("bookName");
					
					System.out.println("Parsed Book ID: " + bookId + ", File Name: " + fileName);

					extractPagesService.uploadBook(bookId, fileName);

				} catch (Exception e) {
					System.err.println("Error processing SQS message: " + e.getMessage());
				}
			});
			return null;
		};
	}
}
