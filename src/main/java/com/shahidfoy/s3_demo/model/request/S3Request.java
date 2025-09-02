package com.shahidfoy.s3_demo.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3Request {

    private String fileName;
    private String contentType;
}
