package com.bookanaudio.books.service;

import com.bookanaudio.books.dto.BookResponse;
import com.bookanaudio.books.dto.BookRequest;
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

    public List<BookResponse> getAllBooks() {
        List<Book> books = bookRepository.findAll();

        if (books == null || books.isEmpty())
            return new ArrayList<>();

        List<BookResponse> allBooks = new ArrayList<>();
        for (Book book : books) {
            BookResponse bookResponse = new BookResponse();

            bookResponse.setId(book.getId());
            bookResponse.setName(book.getName());
            bookResponse.setGenre(book.getGenre());
            bookResponse.setAuthor(book.getAuthor());
            bookResponse.setChapterNamingScheme(book.getChapterNamingScheme());

            allBooks.add(bookResponse);
        }
        return allBooks;
    }

    public BookResponse saveBook(BookRequest bookRequest) {

        Book book = new Book();

        book.setName(bookRequest.getName());
        book.setAuthor(bookRequest.getAuthor());
        book.setGenre(bookRequest.getGenre());
        book.setChapterNamingScheme(bookRequest.getChapterNamingScheme());

        Book savedBook = bookRepository.save(book);

        BookResponse bookResponse = new BookResponse();

        bookResponse.setId(savedBook.getId());
        bookResponse.setName(savedBook.getName());
        bookResponse.setGenre(savedBook.getGenre());
        bookResponse.setAuthor(savedBook.getAuthor());
        bookResponse.setChapterNamingScheme(savedBook.getChapterNamingScheme());

        return bookResponse;
    }

    public List<BookResponse> getAllFilteredBooks(String author, String genre) {
        if (author != null && author.trim().isEmpty())
            author = null;

        if (genre != null && genre.trim().isEmpty())
            genre = null;

        List<Book> books = bookRepository.getAllBooksByFilter(author, genre);

        if (books == null || books.isEmpty())
            return new ArrayList<>();

        List<BookResponse> allBooks = new ArrayList<>();
        for (Book book : books) {
            BookResponse bookResponse = new BookResponse();

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
