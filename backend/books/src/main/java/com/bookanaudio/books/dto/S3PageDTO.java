package com.bookanaudio.books.dto;

import org.springframework.core.io.ByteArrayResource;

public class S3PageDTO {
    private ByteArrayResource fileAsResource;
    private String fileName;
    private Long contentLength;

    public S3PageDTO(ByteArrayResource fileAsResource, String fileName, Long contentLength) {
        this.fileAsResource = fileAsResource;
        this.fileName = fileName;
        this.contentLength = contentLength;
    }

    public ByteArrayResource getFileAsResource() {
        return this.fileAsResource;
    }

    public void setFileAsResource(ByteArrayResource fileAsResource) {
        this.fileAsResource = fileAsResource;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getContentLength() {
        return this.contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

   
}
