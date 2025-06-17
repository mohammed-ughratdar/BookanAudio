package com.bookanaudio.books.service;

import com.bookanaudio.books.dto.BookRequest;
import com.bookanaudio.books.dto.BookResponse;
import com.bookanaudio.books.model.Book;
import com.bookanaudio.books.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BooksServiceTest {

    private BookRepository bookRepository;
    private ExtractPagesService extractPagesService;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        extractPagesService = mock(ExtractPagesService.class);
        bookService = new BookService(bookRepository, extractPagesService);
    }

    @Test
    void testGetAllBooks_ReturnsList() {
        List<Book> books = new ArrayList<>();
        Book book = new Book();
        book.setId(1L);
        book.setName("Test Book");
        book.setAuthor("Author");
        book.setGenre("Genre");
        book.setChapterNamingScheme("Chapter 1");
        books.add(book);

        when(bookRepository.findAll()).thenReturn(books);

        List<BookResponse> responses = bookService.getAllBooks();

        assertEquals(1, responses.size());
        assertEquals("Test Book", responses.get(0).getName());
    }

    @Test
    void testGetAllBooks_ReturnsEmptyList() {
        when(bookRepository.findAll()).thenReturn(new ArrayList<>());

        List<BookResponse> responses = bookService.getAllBooks();

        assertTrue(responses.isEmpty());
    }

    @Test
    void testSaveBook_Success() {
        BookRequest request = new BookRequest();
        request.setName("Test Book");
        request.setAuthor("Author");
        request.setGenre("Genre");
        request.setChapterNamingScheme("Chapter 1");

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setName("Test Book");
        savedBook.setAuthor("Author");
        savedBook.setGenre("Genre");
        savedBook.setChapterNamingScheme("Chapter 1");

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        BookResponse response = bookService.saveBook(request);

        assertEquals(1L, response.getId());
        assertEquals("Test Book", response.getName());
    }

    @Test
    void testGetAllFilteredBooks_ByAuthorAndGenre() {
        List<Book> books = new ArrayList<>();
        Book book = new Book();
        book.setId(1L);
        book.setName("Book 1");
        book.setAuthor("Author");
        book.setGenre("Genre");
        book.setChapterNamingScheme("Scheme");
        books.add(book);

        when(bookRepository.getAllBooksByFilter("Author", "Genre")).thenReturn(books);

        List<BookResponse> responses = bookService.getAllFilteredBooks("Author", "Genre");

        assertEquals(1, responses.size());
        assertEquals("Book 1", responses.get(0).getName());
    }

    @Test
    void testGetAllFilteredBooks_EmptyFilterValues() {
        when(bookRepository.getAllBooksByFilter(null, null)).thenReturn(new ArrayList<>());

        List<BookResponse> responses = bookService.getAllFilteredBooks(" ", "   ");

        assertTrue(responses.isEmpty());
        verify(bookRepository).getAllBooksByFilter(null, null);
    }
}
