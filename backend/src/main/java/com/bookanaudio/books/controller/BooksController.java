package com.bookanaudio.books.controller;

import com.bookanaudio.books.dto.AllBooksResponse;
import com.bookanaudio.books.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BooksController {

    private final BookService bookService;

    @Autowired
    public BooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<AllBooksResponse>> getAllBooks() {
        List<AllBooksResponse> allBooks = bookService.getAllBooks();
        return ResponseEntity.ok(allBooks);
    }
}
