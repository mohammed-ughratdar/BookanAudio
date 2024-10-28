package com.bookanaudio.books.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class S3Service {

    private final AmazonS3 s3Client;
    private final String bucketName;

    public S3Service(
            @Value("${s3.bucket_name}") String bucketName,
            @Value("${s3.access_key}") String accessKey,
            @Value("${s3.secret_key}") String secretKey
    ) {
        this.bucketName = bucketName;
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion("eu-north-1") 
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    public void uploadBook(MultipartFile bookFile) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bookFile.getSize());
            s3Client.putObject(new PutObjectRequest(bucketName, bookFile.getOriginalFilename(), bookFile.getInputStream(), metadata));
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
}
