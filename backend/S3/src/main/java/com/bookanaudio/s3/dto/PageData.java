package com.bookanaudio.s3.dto;

public class PageData {
    private String pageUrl;
    private int pageNumber;
    private Long bookId;
    private String chapterNumber;

    public PageData(String pageUrl, int pageNumber, Long bookId, String chapterNumber) {
        this.pageUrl = pageUrl;
        this.pageNumber = pageNumber;
        this.bookId = bookId;
        this.chapterNumber = chapterNumber;
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

    public String getChapterNumber() {
        return chapterNumber;
    }
}
