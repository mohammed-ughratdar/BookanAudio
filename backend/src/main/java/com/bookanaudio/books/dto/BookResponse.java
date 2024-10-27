package com.bookanaudio.books.dto;

public class BookResponse {
    private Long id;
    private String name;
    private String author;
    private String genre;
    private String chapterNamingScheme;

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
}
