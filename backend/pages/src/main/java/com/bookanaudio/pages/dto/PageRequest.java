package com.bookanaudio.pages.dto;

public class PageRequest {
    private String s3Url;
    private int pageNumber;
    private Long bookId;


    public String getS3Url() {
        return this.s3Url;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
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
    
}
