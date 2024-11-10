package com.bookanaudio.books.service;

import com.bookanaudio.books.dto.PageData;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.HttpMethod;
import java.io.IOException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Service
public class ExtractPagesService {

    private final AmazonS3 s3Client;
    private final String bucketName;
    private final String awsRegion;

    public ExtractPagesService(
            @Value("${s3_bucket_name}") String bucketName,
            @Value("${iam_access_key}") String accessKey,
            @Value("${iam_secret_key}") String secretKey,
            @Value("${aws_region}") String awsRegion
    ) {
        this.bucketName = bucketName;
        this.awsRegion = awsRegion;
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(awsRegion)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    @Async
    public void uploadBook(Long bookId, String fileName) {
    
        String bookDownloadUrl = generatGETPresignedUrl(fileName);

        try (InputStream bookStream = new URL(bookDownloadUrl).openStream();
            PDDocument document = PDDocument.load(bookStream)) {

        
            List<PageData> pagesData = extractPages(document, fileName, bookId);
            
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
                System.out.println("Page URL is " + pageUrl);
                pagesData.add(new PageData(pageUrl, i + 1, bookId));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pagesData;
    }

    public String uploadPage(InputStream inputStream, String fileName, Long contentLength) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, "eu-north-1", fileName);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public URL generatePUTPresignedUrl(String fileName) {
        long expirationTimeMillis = System.currentTimeMillis() + 1000 * 60 * 20; 
        Date expiration = new Date(expirationTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);

        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        return url;
    }

    public String generatGETPresignedUrl(String fileName) {
        GeneratePresignedUrlRequest downloadRequest = new GeneratePresignedUrlRequest(bucketName, fileName)
            .withMethod(HttpMethod.GET)
            .withExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60));
        URL downloadUrl = s3Client.generatePresignedUrl(downloadRequest);
        return downloadUrl.toString();  
    }
}
