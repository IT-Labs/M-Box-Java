package com.app.MBox.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.app.MBox.core.model.users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import com.app.MBox.config.amazonS3Config;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class amazonS3ClientServiceImpl implements amazonS3ClientService {

  //  private String awsS3AudioBucket;
    private AmazonS3 amazonS3;
    @Autowired private amazonS3Config amazonS3Config;

    @PostConstruct
    public void init()
    {
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(amazonS3Config.getAWSCredentials())
                .withRegion(amazonS3Config.getAWSPollyRegion().getName()).build();
       // this.awsS3AudioBucket = awsS3AudioBucket;
    }

    @Async
    public void uploadFileToS3Bucket(MultipartFile multipartFile, boolean enablePublicReadAccess,String imageName)
    {
        String fileName = multipartFile.getOriginalFilename();

        try {
            //creating the file in the server (temporarily)
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();

            PutObjectRequest putObjectRequest = new PutObjectRequest(amazonS3Config.getAWSS3AudioBucket(), imageName, file);

            if (enablePublicReadAccess) {
                putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
            }
            this.amazonS3.putObject(putObjectRequest);

            //removing the file created in the server

            file.delete();

        } catch (IOException | AmazonServiceException ex) {
            log.error("error [" + ex.getMessage() + "] occured while uploading [" + fileName + "] ");
        }


    }

    @Async
    public void deleteFileFromS3Bucket(String fileName)
    {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(amazonS3Config.getAWSS3AudioBucket(), fileName));
        } catch (AmazonServiceException ex) {
            log.error("error [" + ex.getMessage() + "] occurred while removing [" + fileName + "] ");
        }
    }


    public String getPictureUrl(String fileName) {
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);
        GeneratePresignedUrlRequest request=new GeneratePresignedUrlRequest(amazonS3Config.getAWSS3AudioBucket(),fileName).withMethod(HttpMethod.GET).withExpiration(expiration);
        URL url=amazonS3.generatePresignedUrl(request);
        return url.toString();
    }
}
