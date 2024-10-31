package com.bookanaudio.pages.service;

import com.bookanaudio.pages.dto.PageResponse;
import com.bookanaudio.pages.dto.PageRequest;
import com.bookanaudio.pages.exception.PageException;
import com.bookanaudio.pages.model.Page;
import com.bookanaudio.pages.repository.PageRepository;
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

    public List<PageResponse> getAllPages(Long bookId) {
        List<Page> pages = pageRepository.findByBookId(bookId);

        if (pages == null || pages.isEmpty()) {
            return new ArrayList<>();
        }

        List<PageResponse> allPages = new ArrayList<>();
        for (Page page : pages) {
            PageResponse pageResponse = new PageResponse();
            pageResponse.setId(page.getId());
            pageResponse.setPageNumber(page.getPageNumber());
            pageResponse.setBookId(page.getBookId());
            pageResponse.setS3Url(page.getS3Url());

            allPages.add(pageResponse);
        }
        return allPages;
    }

    public void savePages(List<PageRequest> pageRequests) {
        for (PageRequest pageRequest : pageRequests) {
            Page page = new Page();
            page.setPageNumber(pageRequest.getPageNumber());
            page.setBookId(pageRequest.getBookId());
            page.setS3Url(pageRequest.getS3Url());

            pageRepository.save(page);
        }
    }
}
