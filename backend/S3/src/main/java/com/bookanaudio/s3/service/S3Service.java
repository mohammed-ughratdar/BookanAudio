package com.bookanaudio.s3.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class S3Service {

    private final AmazonS3 s3Client;
    private final String bucketName;
    private final String awsRegion;

    public S3Service(
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
    
    public String uploadPage(InputStream inputStream, String fileName, Long contentLength) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, awsRegion, fileName);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
