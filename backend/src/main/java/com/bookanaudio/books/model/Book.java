package com.bookanaudio.books.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String author;
    private String genre;

    @Column(name = "chapter_naming_scheme")
    private String chapterNamingScheme;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return this.genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getChapterNamingScheme() {
        return this.chapterNamingScheme;
    }

    public void setChapterNamingScheme(String chapterNamingScheme) {
        this.chapterNamingScheme = chapterNamingScheme;
    }

    public Boolean isPublic() {
        return this.isPublic;
    }

    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public LocalDateTime getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
