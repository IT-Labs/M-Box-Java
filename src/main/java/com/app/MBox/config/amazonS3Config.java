package com.app.MBox.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class amazonS3Config {

    @Value("${aws.access.key.id}")
    private String awsKeyId;

    @Value("${aws.access.key.secret}")
    private String awsKeySecret;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.s3.audio.bucket}")
    private String awsS3AudioBucket;

    public Region getAWSPollyRegion() {
        return Region.getRegion(Regions.fromName(awsRegion));
    }

    public AWSCredentialsProvider getAWSCredentials() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.awsKeyId, this.awsKeySecret);
        return new AWSStaticCredentialsProvider(awsCredentials);
    }

    public String getAWSS3AudioBucket() {
        return awsS3AudioBucket;
    }

}
