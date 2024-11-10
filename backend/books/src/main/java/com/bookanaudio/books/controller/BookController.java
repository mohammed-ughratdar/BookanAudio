package com.bookanaudio.books.controller;

import com.bookanaudio.books.dto.BookResponse;
import com.bookanaudio.books.dto.BookRequest;
import com.bookanaudio.books.service.BookService;
import com.bookanaudio.books.service.ExtractPagesService;
import com.bookanaudio.books.service.SQSService;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final ExtractPagesService extractPagesService;
    private final SQSService sqsService;  

    @Autowired
    public BookController(BookService bookService, 
                          ExtractPagesService extractPagesService,
                          SQSService sqsService
                        ) {
        this.bookService = bookService;
        this.extractPagesService = extractPagesService;
        this.sqsService = sqsService;
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> allBooks = bookService.getAllBooks();
        return ResponseEntity.ok(allBooks);
    }

    @PostMapping
    public ResponseEntity<BookResponse> saveBook(@RequestBody BookRequest bookRequest) {

        BookResponse bookResponse = bookService.saveBook(bookRequest);
        sqsService.sendMessageToSQS(bookResponse.getId(), bookRequest.getName());

        return ResponseEntity.ok(bookResponse);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<BookResponse>> getAllFilteredBooks(@RequestParam("author") String author,
                                                                  @RequestParam("genre") String genre) {
        List<BookResponse> allBooks = bookService.getAllFilteredBooks(author, genre);
        return ResponseEntity.ok(allBooks);
    }

    @GetMapping("/pre-signed-url")
    public ResponseEntity<String> getPreSignedUrl(@RequestParam("filename") String fileName) {
        URL url = extractPagesService.generatePUTPresignedUrl(fileName);
        return ResponseEntity.ok(url.toString());
    }

    @GetMapping("/check-net")
    public String checkNetworkConnectivity() {
        String testUrl = "https://www.google.com";
        try {
            URL url = new URL(testUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);  
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return "Lambda has internet connectivity!";
            } else {
                return "Failed to connect to " + testUrl + ". Response code: " + responseCode;
            }
        } catch (IOException e) {
            return "Network connectivity test failed: " + e.getMessage();
        }
    }
}
