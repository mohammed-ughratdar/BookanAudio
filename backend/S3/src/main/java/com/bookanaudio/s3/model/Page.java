package com.bookanaudio.pages.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pages")
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "page_number", nullable = false)
    private int pageNumber;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "s3_url", nullable = false)
    private String s3Url;

    private String chapter;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Long getBookId() {
        return this.bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getS3Url() {
        return this.s3Url;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }

    public String getChapter() {
        return this.chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

}
