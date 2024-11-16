package com.bookanaudio.s3.service;

import com.bookanaudio.s3.dto.PageData;
import com.bookanaudio.s3.exception.S3Exception;
import com.bookanaudio.s3.model.Page;
import com.bookanaudio.s3.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class PageService {

    private final PageRepository pageRepository;

    @Autowired
    public PageService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public void savePage(PageData pageData) {
        Page page = new Page();
        page.setPageNumber(pageData.getPageNumber());
        page.setBookId(pageData.getBookId());
        page.setS3Url(pageData.getPageUrl());
        page.setChapter(pageData.getChapterNumber());

        pageRepository.save(page);
        
    }
}
