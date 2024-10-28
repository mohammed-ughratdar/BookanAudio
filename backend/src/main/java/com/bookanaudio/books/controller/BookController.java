package com.bookanaudio.books.controller;

import com.bookanaudio.books.dto.BookResponse;
import com.bookanaudio.books.dto.BookRequest;
import com.bookanaudio.books.service.BookService;
import com.bookanaudio.books.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final S3Service s3Service;

    @Autowired
    public BookController(BookService bookService, S3Service s3Service) {
        this.bookService = bookService;
        this.s3Service = s3Service;
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> allBooks = bookService.getAllBooks();
        return ResponseEntity.ok(allBooks);
    }

    @PostMapping
    public ResponseEntity<BookResponse> saveBook(@RequestBody BookRequest bookRequest) {
        BookResponse book = bookService.saveBook(bookRequest);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<BookResponse>> getAllFilteredBooks(@RequestParam("author") String author,
                                                          @RequestParam("genre") String genre) {
        List<BookResponse> allBooks = bookService.getAllFilteredBooks(author, genre);
        return ResponseEntity.ok(allBooks);
    }

    @PostMapping("/upload")
    public ResponseEntity uploadBook(@RequestParam("file") MultipartFile bookFile) {
        s3Service.uploadBook(bookFile);
        return ResponseEntity.ok("File uploaded successfully");
    }

}
