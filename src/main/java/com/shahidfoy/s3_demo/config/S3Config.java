package com.shahidfoy.s3_demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Getter
@Configuration
public class S3Config {

    @Value("${storage.s3.endpoint}")
    private String STORAGE_S3_ENDPOINT;

    @Value("${storage.s3.access-key}")
    private String STORAGE_S3_ACCESS_KEY;

    @Value("${storage.s3.secret-key}")
    private String STORAGE_S3_SECRET_KEY;

    @Value("${storage.s3.region")
    private String STORAGE_S3_REGION;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(STORAGE_S3_ACCESS_KEY, STORAGE_S3_SECRET_KEY);

        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .endpointOverride(URI.create(STORAGE_S3_ENDPOINT))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .region(Region.of(STORAGE_S3_REGION))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(STORAGE_S3_ACCESS_KEY, STORAGE_S3_SECRET_KEY);

        return S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .endpointOverride(URI.create(STORAGE_S3_ENDPOINT))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .region(Region.of(STORAGE_S3_REGION))
                .build();
    }
}
