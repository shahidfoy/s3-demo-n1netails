package com.shahidfoy.s3_demo.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3RenameRequest {

    private String oldFileName;
    private String newFileName;
    private String contentType;
}
