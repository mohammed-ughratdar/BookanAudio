package com.bookanaudio.pages.service;

import com.bookanaudio.pages.dto.PageRequest;
import com.bookanaudio.pages.dto.PageResponse;
import com.bookanaudio.pages.model.Page;
import com.bookanaudio.pages.repository.PageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PagesServiceTest {

    @Mock
    private PageRepository pageRepository;

    @InjectMocks
    private PageService pageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllPages_shouldReturnListOfPageResponses_whenPagesExist() {
        Long bookId = 1L;

        Page page1 = new Page();
        page1.setId(1L);
        page1.setPageNumber(1);
        page1.setBookId(bookId);
        page1.setS3Url("s3://url/1");

        Page page2 = new Page();
        page2.setId(2L);
        page2.setPageNumber(2);
        page2.setBookId(bookId);
        page2.setS3Url("s3://url/2");

        when(pageRepository.findByBookId(bookId)).thenReturn(List.of(page1, page2));

        List<PageResponse> responses = pageService.getAllPages(bookId);

        assertEquals(2, responses.size());
        assertEquals("s3://url/1", responses.get(0).getS3Url());
    }


    @Test
    void getAllPages_shouldReturnEmptyList_whenNoPagesExist() {
        Long bookId = 1L;

        when(pageRepository.findByBookId(bookId)).thenReturn(Collections.emptyList());

        List<PageResponse> responses = pageService.getAllPages(bookId);

        assertTrue(responses.isEmpty());
    }

    @Test
    void savePages_shouldSaveAllGivenPageRequests() {
        PageRequest request1 = new PageRequest();
        request1.setPageNumber(1);
        request1.setBookId(1L);
        request1.setS3Url("s3://url/1");

        PageRequest request2 = new PageRequest();
        request2.setPageNumber(2);
        request2.setBookId(1L);
        request2.setS3Url("s3://url/2");

        List<PageRequest> pageRequests = List.of(request1, request2);

        pageService.savePages(pageRequests);

        verify(pageRepository, times(2)).save(any(Page.class));
    }

}
