package com.bookanaudio.s3.service;

import com.bookanaudio.s3.dto.PageData;
import com.bookanaudio.s3.service.PageService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import com.amazonaws.HttpMethod;
import java.io.IOException;
import org.apache.pdfbox.text.PDFTextStripper;
import com.bookanaudio.s3.service.OpenAiService;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
    private final String ocrApiUrl;
    private final String ocrApiKey;
    private final PageService pageService;
    private final OpenAiService openAiService;

    @Autowired
    public ExtractPagesService(
            @Value("${s3_bucket_name}") String bucketName,
            @Value("${iam_access_key}") String accessKey,
            @Value("${iam_secret_key}") String secretKey,
            @Value("${aws_region}") String awsRegion,
            @Value("${ocr_api_url}") String ocrApiUrl,
            @Value("${ocr_api_key}") String ocrApiKey,
            PageService pageService,
            OpenAiService openAiService
    ) {
        this.pageService = pageService;
        this.openAiService = openAiService;
        this.bucketName = bucketName;
        this.awsRegion = awsRegion;
        this.ocrApiUrl = ocrApiUrl;
        this.ocrApiKey = ocrApiKey;
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(awsRegion)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    public void uploadBook(Long bookId, String fileName) {
    
        String bookDownloadUrl = generatGETPresignedUrl(fileName);

        try (InputStream bookStream = new URL(bookDownloadUrl).openStream();
            PDDocument document = PDDocument.load(bookStream)) {

            extractPages(document, fileName, bookId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void extractPages(PDDocument document, String bookName, Long bookId) {
        int totalPages = document.getNumberOfPages();

        for (int i = 0; i < totalPages; i++) {
            try (PDDocument singlePageDocument = new PDDocument()) {
                PDPage page = document.getPage(i);
                singlePageDocument.addPage(page);

                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                singlePageDocument.save(outStream);

                String extractedText = extractTextUsingOCR(outStream.toByteArray());
                System.out.println("Extracted Text: "+ extractedText);
                String chapterNumber = openAiService.getChapterNumber(extractedText);
                String fileName = String.format("%s/page_%d.pdf", bookName, i + 1);

                String pageUrl = uploadPage(new ByteArrayInputStream(outStream.toByteArray()), fileName, (long) outStream.size());
                System.out.println("Page URL is " + pageUrl);
                if(pageUrl == null) continue;
                PageData pageData = new PageData(pageUrl, i + 1, bookId, chapterNumber);
                pageService.savePage(pageData);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public String generatGETPresignedUrl(String fileName) {
        GeneratePresignedUrlRequest downloadRequest = new GeneratePresignedUrlRequest(bucketName, fileName)
            .withMethod(HttpMethod.GET)
            .withExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60));
        URL downloadUrl = s3Client.generatePresignedUrl(downloadRequest);
        return downloadUrl.toString();  
    }

    public String extractTextUsingOCR(byte[] pdfContent) {
        try {
            String base64Content = Base64.getEncoder().encodeToString(pdfContent);
            String base64Image = String.format("data:application/pdf;base64,%s", base64Content);

            String urlParameters = String.format("base64Image=%s&filetype=pdf", 
                    URLEncoder.encode(base64Image, "UTF-8"));

            URL url = new URL(ocrApiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("apikey", ocrApiKey);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(urlParameters.getBytes("UTF-8"));
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            String responseString = response.toString();

            if (responseString.contains("\"ParsedResults\":")) {
                String parsedText = responseString.split("\"ParsedText\":\"")[1].split("\"")[0];
                return parsedText.replace("\\n", "\n"); 
            } else {
                System.err.println("OCR response does not contain parsed text.");
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
