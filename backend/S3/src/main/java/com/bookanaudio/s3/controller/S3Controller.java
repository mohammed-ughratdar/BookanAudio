package com.bookanaudio.s3.controller;

import com.bookanaudio.s3.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


@RestController
@RequestMapping("/s3")
public class S3Controller {

    private final S3Service s3Service;

    @Autowired
    public S3Controller(S3Service s3Service){
        this.s3Service = s3Service;
    }

   @PostMapping("/upload_page")
    public ResponseEntity<String> uploadPage(@RequestParam("file") MultipartFile file,
                                         @RequestParam("fileName") String fileName,
                                         @RequestParam("contentLength") Long contentLength) {
        try (InputStream inputStream = file.getInputStream()) {
            String s3Url = s3Service.uploadPage(inputStream, fileName, contentLength);
            return ResponseEntity.ok(s3Url);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error uploading the file");
        }
    }

}
