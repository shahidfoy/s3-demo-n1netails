package com.shahidfoy.s3_demo.service.impl;

import com.shahidfoy.s3_demo.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service implements S3StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Override
    public void saveFileToBucket(String bucket, String fileName, String contentType, byte[] bytes) {
        log.debug("== S3 saveFileToBucket");
        log.debug("= file name: {}", fileName);
        log.debug("= bucket: {}", bucket);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
    }

    @Override
    public byte[] getFileFromBucket(String bucket, String fileName) {
        log.debug("== S3 getFileFromBucket");
        log.debug("= file name: {}", fileName);
        log.debug("= bucket: {}", bucket);
        GetObjectRequest getObjectRequest= GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        return s3Client.getObject(getObjectRequest, ResponseTransformer.toBytes()).asByteArray();
    }

    @Override
    public String generatePresignedUrl(String bucket, String fileName, String contentType) {
        log.debug("== Generate Presigned Url filename: {}", fileName);
        log.debug("= bucket: {}", bucket);
        Duration expiration = Duration.ofMinutes(15);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .contentType(contentType)
                .build();
        log.debug("== presignedGetObjectRequest");
        PresignedPutObjectRequest presignedPutObjectRequest = this.s3Presigner.presignPutObject(putRequest ->
                putRequest
                        .signatureDuration(expiration)
                        .putObjectRequest(putObjectRequest)
        );
        log.debug("== Generate Presigned Url returning");
        return presignedPutObjectRequest.url().toString();
    }

    @Override
    public void renameFile(String bucket, String oldFileName, String newFileName, String contentType) {
        log.debug("== renameFile");
        log.debug("renameFile old file name: {}", oldFileName);
        log.debug("renameFile new file name: {}", newFileName);
        log.debug("= bucket: {}", bucket);
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(oldFileName)
                    .build();

            byte[] fileBytes = s3Client.getObject(getRequest, ResponseTransformer.toBytes()).asByteArray();
            this.saveFileToBucket(bucket, newFileName, contentType, fileBytes);
            log.debug("Object copied!");
        } catch (S3Exception e) {
            log.error("Object not found: " + e.awsErrorDetails().errorMessage());
            throw e;
        } catch (Exception ex) {
            log.error("Exception Object not found: " + ex.getMessage());
            throw ex;
        }

        try {
            this.deleteFile(bucket, oldFileName);
            log.debug("Old file deleted: " + oldFileName);
            log.debug("File has been moved");
        } catch (S3Exception e) {
            log.error("Error moving file:");
            log.error("Failed to rename and copy file: {}", e.awsErrorDetails().errorMessage(), e);
            throw e;
        } catch (Exception ex) {
            log.error("Exception Error moving file:");
            log.error("Exception Failed to rename and copy file: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void deleteFile(String bucket, String fileName) {
        log.debug("deleteFile: {}", fileName);
        log.debug("= bucket: {}", bucket);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public void makeFilePublic(String bucket, String fileName) {
        log.debug("makeFilePublic: {}", fileName);
        log.debug("= bucket: {}", bucket);
        try {
            // Create a PutObjectAclRequest to set the object to PUBLIC_READ
            PutObjectAclRequest putObjectAclRequest = PutObjectAclRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .acl(ObjectCannedACL.PUBLIC_READ)  // Set the ACL to public
                    .build();

            // Set the object ACL
            s3Client.putObjectAcl(putObjectAclRequest);
            log.debug("File has been made public.");
        }
        catch (S3Exception e) {
            log.error("Error setting file to public:");
            log.error(e.awsErrorDetails().errorMessage());
            throw e;
        } catch (Exception ex) {
            log.error("Exception Error setting file to public:");
            log.error(ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void makeFilePrivate(String bucket, String fileName) {
        log.debug("makeFilePrivate: {}", fileName);
        log.debug("= bucket: {}", bucket);
        try {
            // Create a PutObjectAclRequest to set the object to PRIVATE
            PutObjectAclRequest putObjectAclRequest = PutObjectAclRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .acl(ObjectCannedACL.PRIVATE)  // Set the ACL to private
                    .build();

            // Set the object ACL
            s3Client.putObjectAcl(putObjectAclRequest);
            log.debug("File has been made private.");
        }
        catch (S3Exception e) {
            log.error("Error setting file to private:");
            log.error(e.awsErrorDetails().errorMessage());
            throw e;
        } catch (Exception ex) {
            log.error("Exception Error setting file to private:");
            log.error(ex.getMessage());
            throw ex;
        }
    }
}
