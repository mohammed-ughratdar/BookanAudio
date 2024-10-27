package com.bookanaudio.books.controller;

import com.bookanaudio.books.dto.BookResponse;
import com.bookanaudio.books.dto.BookRequest;
import com.bookanaudio.books.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
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
}
