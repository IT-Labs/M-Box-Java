package com.app.MBox.services;

import org.springframework.web.multipart.MultipartFile;

public interface amazonS3ClientService {

    void uploadFileToS3Bucket(MultipartFile multipartFile, boolean enablePublicReadAccess);

    void deleteFileFromS3Bucket(String fileName);
}
