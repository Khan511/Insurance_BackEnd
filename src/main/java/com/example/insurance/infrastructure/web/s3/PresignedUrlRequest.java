package com.example.insurance.infrastructure.web.s3;

import lombok.Data;

@Data
public class PresignedUrlRequest {

    private String fileName;
    private String fileType;

}