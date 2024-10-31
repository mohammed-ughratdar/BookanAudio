package com.bookanaudio.pages.dto;

public class PageResponse {
    private Long id;
    private int pageNumber;
    private Long bookId;
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
