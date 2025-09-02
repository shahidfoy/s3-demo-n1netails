package com.shahidfoy.s3_demo.service;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import software.amazon.awssdk.services.s3.model.S3Exception;

public interface S3StorageService {

    /**
     * save file byte contents to s3 bucket
     * @param bucket bucket path
     * @param fileName file name
     * @param contentType content type
     * @param bytes file bytes
     */
    void saveFileToBucket(String bucket, String fileName, String contentType, byte[] bytes);

    /**
     * retrieves file from target bucket
     * @param bucket bucket path
     * @param fileName file name
     * @return file in bytes
     */
    byte[] getFileFromBucket(String bucket, String fileName);

    /**
     * generates presigned temp url to upload to s3 bucket
     * @param bucket bucket path
     * @param fileName target file name
     * @param contentType content type
     * @return presigned url sent to frontend
     */
    String generatePresignedUrl(String bucket, String fileName, String contentType);

    /**
     * copies old file contents into new file then deletes old file
     * @param bucket bucket path
     * @param oldFileName old file name on s3
     * @param newFileName new file name
     * @param contentType content type
     */
    @Retryable(
            retryFor = { S3Exception.class, Exception.class },
            maxAttempts = 10,
            backoff = @Backoff(delay = 2000)
    )
    void renameFile(String bucket, String oldFileName, String newFileName, String contentType);

    /**
     * deletes target file
     * @param bucket bucket path
     * @param fileName name of file to be deleted
     */
    void deleteFile(String bucket, String fileName);

    /**
     * gives the file public view permission
     * @param bucket bucket path
     * @param fileName file name
     */
    @Retryable(
            retryFor = { S3Exception.class, Exception.class },
            maxAttempts = 10,
            backoff = @Backoff(delay = 2000)
    )
    void makeFilePublic(String bucket, String fileName);

    /**
     * makes the file private
     * @param bucket bucket path
     * @param fileName file name
     */
    @Retryable(
            retryFor = { S3Exception.class, Exception.class },
            maxAttempts = 10,
            backoff = @Backoff(delay = 2000)
    )
    public void makeFilePrivate(String bucket, String fileName);
}
