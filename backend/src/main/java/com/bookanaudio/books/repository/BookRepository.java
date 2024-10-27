package com.bookanaudio.books.repository;

import com.bookanaudio.books.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    Book findByName(String name);
}
