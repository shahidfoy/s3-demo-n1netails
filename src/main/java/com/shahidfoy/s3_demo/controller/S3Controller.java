package com.shahidfoy.s3_demo.controller;

import com.shahidfoy.s3_demo.model.request.S3RenameRequest;
import com.shahidfoy.s3_demo.model.request.S3Request;
import com.shahidfoy.s3_demo.model.response.S3Response;
import com.shahidfoy.s3_demo.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = {"/api/s3bucket"})
public class S3Controller {

    @Value("${storage.s3.cdn-endpoint}")
    private String STORAGE_S3_CDN_ENDPOINT;
    @Value("${storage.s3.endpoint}")
    private String STORAGE_S3_ENDPOINT;
    @Value("${storage.s3.bucket-name}")
    private String STORAGE_S3_BUCKET;

    private final S3StorageService service;

    @PostMapping(value = "/save-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<S3Response> saveFile(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();

        if (file.isEmpty())
            throw new IOException("The provided file is empty or null.");
        this.service.saveFileToBucket(STORAGE_S3_BUCKET, fileName, contentType, file.getBytes());

        String bucketUrl = STORAGE_S3_CDN_ENDPOINT + "/" + STORAGE_S3_BUCKET + "/" + fileName;
        return new ResponseEntity<>(new S3Response(bucketUrl), OK);
    }

    @GetMapping("/get-file/{fileName}")
    public ResponseEntity<S3Response> getFile(@PathVariable String fileName) {

        byte[] bytes = this.service.getFileFromBucket(STORAGE_S3_BUCKET, fileName);
        String b64 = Base64.getEncoder().encodeToString(bytes);
        return new ResponseEntity<>(new S3Response(b64), OK);
    }

    @GetMapping("/get-file-url/{fileName}")
    public ResponseEntity<S3Response> getFileUrl(@PathVariable String fileName) {

        String bucketUrl = STORAGE_S3_CDN_ENDPOINT + "/" + STORAGE_S3_BUCKET + "/" + fileName;
        return new ResponseEntity<>(new S3Response(bucketUrl), OK);
    }

    @PutMapping("/rename-file")
    public ResponseEntity<S3Response> renameFile(
            @RequestBody S3RenameRequest s3RenameRequest) {

        this.service.renameFile(STORAGE_S3_BUCKET, s3RenameRequest.getOldFileName(),
                s3RenameRequest.getNewFileName(), s3RenameRequest.getContentType());
        return new ResponseEntity<>(new S3Response("File renamed successfully"), OK);
    }

    @DeleteMapping("/delete-file/{fileName}")
    public ResponseEntity<S3Response> deleteFile(@PathVariable String fileName) {

        this.service.deleteFile(STORAGE_S3_BUCKET, fileName);
        return new ResponseEntity<>(new S3Response("File deleted"), OK);
    }

    @PatchMapping("/make-file-public/{fileName}")
    public ResponseEntity<S3Response> makeFilePublic(@PathVariable String fileName) {

        this.service.makeFilePublic(STORAGE_S3_BUCKET, fileName);
        return new ResponseEntity<>(new S3Response("File made public"), OK);
    }

    @PatchMapping("/make-file-private/{fileName}")
    public ResponseEntity<S3Response> makeFilePrivate(@PathVariable String fileName) {

        this.service.makeFilePrivate(STORAGE_S3_BUCKET, fileName);
        return new ResponseEntity<>(new S3Response("File made private"), OK);
    }

    @PostMapping("/get-temp-s3-url")
    public ResponseEntity<S3Response> getTempS3Url(
            @RequestBody S3Request s3Request) {

        String tempUrl = this.service.generatePresignedUrl(
                STORAGE_S3_BUCKET, s3Request.getFileName(), s3Request.getContentType());
        log.debug("== temp url: {}", tempUrl);
        return new ResponseEntity<>(new S3Response(tempUrl), OK);
    }
}
