package com.bookanaudio.books.dto;

public class PageData {
    private String pageUrl;
    private int pageNumber;
    private Long bookId;

    public PageData(String pageUrl, int pageNumber, Long bookId) {
        this.pageUrl = pageUrl;
        this.pageNumber = pageNumber;
        this.bookId = bookId;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public Long getBookId() {
        return bookId;
    }
}
