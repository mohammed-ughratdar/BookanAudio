package com.bookanaudio.books.service;

import com.bookanaudio.books.dto.AllBooksResponse;
import com.bookanaudio.books.exception.BookException;
import com.bookanaudio.books.model.Book;
import com.bookanaudio.books.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<AllBooksResponse> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        
        if (books == null || books.isEmpty()) {
            return new ArrayList<>();
        }

        List<AllBooksResponse> allBooks = new ArrayList<>(); 
        for (Book book : books) {
            AllBooksResponse bookResponse = new AllBooksResponse();

            bookResponse.setId(book.getId());
            bookResponse.setName(book.getName());
            bookResponse.setGenre(book.getGenre());
            bookResponse.setAuthor(book.getAuthor());
            bookResponse.setChapterNamingScheme(book.getChapterNamingScheme());

            allBooks.add(bookResponse);
        }
        return allBooks;
    }
}
