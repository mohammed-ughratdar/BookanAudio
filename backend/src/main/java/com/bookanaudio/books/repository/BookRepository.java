package com.bookanaudio.books.repository;

import com.bookanaudio.books.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    Book findByName(String name);

    @Query("SELECT b FROM Book b WHERE (:author IS NULL OR b.author = :author) AND (:genre IS NULL OR b.genre = :genre)")
    List<Book> getAllBooksByFilter(@Param("author") String author, @Param("genre") String genre);

}
