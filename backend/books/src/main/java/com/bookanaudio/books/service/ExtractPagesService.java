package com.bookanaudio.books.service;

import com.bookanaudio.books.dto.PageData;
import com.bookanaudio.books.dto.S3PageDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExtractPagesService {

    private final RestTemplate restTemplate;

    @Value("${s3_module_url}")
    private String s3ModuleUrl;

    @Autowired
    public ExtractPagesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void uploadBook(MultipartFile bookFile, Long bookId) {
        String bookName = bookFile.getOriginalFilename();
        try (PDDocument document = PDDocument.load(bookFile.getInputStream())) {
            List<PageData> pagesData = extractPages(document, bookName, bookId);
            pagesData.forEach(page -> 
                System.out.println("Book ID: " + page.getBookId() +
                                   ", URL: " + page.getPageUrl() +
                                   ", Page Number: " + page.getPageNumber())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<PageData> extractPages(PDDocument document, String bookName, Long bookId) {
        int totalPages = document.getNumberOfPages();
        List<PageData> pagesData = new ArrayList<>();

        for (int i = 0; i < totalPages; i++) {
            try (PDDocument singlePageDocument = new PDDocument()) {
                PDPage page = document.getPage(i);
                singlePageDocument.addPage(page);

                String fileName = String.format("%s/page_%d.pdf", bookName, i + 1);

                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                singlePageDocument.save(outStream);
                String pageUrl = uploadPage(new ByteArrayInputStream(outStream.toByteArray()), fileName, (long) outStream.size());

                pagesData.add(new PageData(pageUrl, i + 1, bookId));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pagesData;
    }

    public String uploadPage(InputStream inputStream, String fileName, Long contentLength) {
        try {
            ByteArrayResource fileResource = new ByteArrayResource(inputStream.readAllBytes()){
                @Override
                public String getFilename() {
                    return fileName; 
                }
            };

            S3PageDTO s3PageDTO = new S3PageDTO(fileResource, fileName, contentLength);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", s3PageDTO.getFileAsResource());
            body.add("fileName", s3PageDTO.getFileName());
            body.add("contentLength", s3PageDTO.getContentLength());
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(s3ModuleUrl, requestEntity, String.class);

            return response.getBody();
        } catch (IOException e) {
            e.printStackTrace();
            return null; 
        }
    }
}
