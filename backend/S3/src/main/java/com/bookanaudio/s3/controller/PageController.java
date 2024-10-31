package com.bookanaudio.pages.controller;

import com.bookanaudio.pages.dto.PageResponse;
import com.bookanaudio.pages.dto.PageRequest;
import com.bookanaudio.pages.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class PageController {

    private final PageService pageService;

    @Autowired
    public PageController(PageService pageService){
        this.pageService = pageService;
    }

    @GetMapping("/{book_id}/pages")
    public ResponseEntity<List<PageResponse>> getAllPages(@PathVariable("book_id") Long bookId) {
        List<PageResponse> allPages = pageService.getAllPages(bookId);
        return ResponseEntity.ok(allPages);
    }

    @PostMapping("/{book_id}/pages")
    public ResponseEntity<String> savePages(@PathVariable("book_id") Long bookId, @RequestBody List<PageRequest> pagesRequest) {
        pageService.savePages(pagesRequest);
        return ResponseEntity.ok("Pages saved successfully");
    }
}
