package com.shahidfoy.s3_demo.service.impl;

import com.n1netails.n1netails.kuda.api.Tail;
import com.shahidfoy.s3_demo.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
@Primary
public class S3ServiceSubtleErrors implements S3StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Async
    @Override
    public void saveFileToBucket(String bucket, String fileName, String contentType, byte[] bytes) {
        log.debug("== S3 saveFileToBucket: {}", fileName);

        // Subtle trigger: throw exception if filename starts with "__error__"
        if (fileName.startsWith("__error__")) {
            throw new IllegalStateException("Subtle failure saving file: " + fileName);
        }

        saveFileNotification(bucket, fileName, contentType);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(contentType)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
    }

    private static void saveFileNotification(String bucket, String fileName, String contentType) {
        Map<String, String> saveFileTags = new HashMap<>();
        saveFileTags.put("filename", fileName);
        saveFileTags.put("bucket", bucket);
        Tail.info("New save file request")
                .description("A new save file request has been received")
                .details(
                        bucket + " " + fileName + " " + contentType
                )
                .type("USER_ACTION_COMPLETED")
                .withTags(saveFileTags)
                .send();
    }

    @Override
    public byte[] getFileFromBucket(String bucket, String fileName) {
        log.debug("== S3 getFileFromBucket: {}", fileName);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        byte[] data = s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes()).asByteArray();

        // Subtle trigger: return stack trace if bytes are empty
        if (data.length == 0) {
            throw new RuntimeException("Retrieved empty file contents for: " + fileName);
        }

        // Subtle trigger: fail on special suffix
        if (fileName.endsWith("__fail__")) {
            throw new RuntimeException("Simulated error retrieving file: " + fileName);
        }

        return data;
    }

    @Override
    public String generatePresignedUrl(String bucket, String fileName, String contentType) {
        log.debug("== Generate Presigned Url for: {}", fileName);

        Duration expiration = Duration.ofMinutes(15);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(contentType)
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest = this.s3Presigner.presignPutObject(
                putRequest -> putRequest.signatureDuration(expiration).putObjectRequest(putObjectRequest)
        );

        String url = presignedPutObjectRequest.url().toString();

        // Subtle trigger: throw exception if bucket name contains "bad"
        if (bucket.contains("bad")) {
            throw new IllegalArgumentException("Invalid presigned URL generation for bucket: " + bucket);
        }

        return url;
    }

    @Override
    public void renameFile(String bucket, String oldFileName, String newFileName, String contentType) {
        log.debug("== renameFile: {} -> {}", oldFileName, newFileName);

        byte[] bytes = this.getFileFromBucket(bucket, oldFileName);
        this.saveFileToBucket(bucket, newFileName, contentType, bytes);

        // Subtle trigger: fail after copy, before delete
        if (oldFileName.endsWith("__rename_fail__")) {
            throw new IllegalStateException("Rename failed after copy for: " + oldFileName);
        }

        this.deleteFile(bucket, oldFileName);
    }

    @Override
    public void deleteFile(String bucket, String fileName) {
        log.debug("== deleteFile: {}", fileName);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        // Subtle trigger: fail only for files containing "undeletable"
        if (fileName.contains("undeletable")) {
            throw new RuntimeException("Subtle failure deleting file: " + fileName);
        }
    }

    @Override
    public void makeFilePublic(String bucket, String fileName) {
        log.debug("== makeFilePublic: {}", fileName);

        PutObjectAclRequest putObjectAclRequest = PutObjectAclRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3Client.putObjectAcl(putObjectAclRequest);

        // Subtle trigger: fail on files containing "nopublic"
        if (fileName.contains("nopublic")) {
            throw new RuntimeException("Could not set file to public: " + fileName);
        }
    }

    @Override
    public void makeFilePrivate(String bucket, String fileName) {
        log.debug("== makeFilePrivate: {}", fileName);

        PutObjectAclRequest putObjectAclRequest = PutObjectAclRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .acl(ObjectCannedACL.PRIVATE)
                .build();

        s3Client.putObjectAcl(putObjectAclRequest);

        // Subtle trigger: fail on files containing "noprivate"
        if (fileName.contains("noprivate")) {
            throw new RuntimeException("Could not set file to private: " + fileName);
        }
    }
}