package com.asecapt.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
    
    @Value("${asecapt.aws.access.key.id:${ASECAPT_AWS_ACCESS_KEY_ID}}")
    private String accessKeyId;
    
    @Value("${asecapt.aws.secret.access.key:${ASECAPT_AWS_SECRET_ACCESS_KEY}}")
    private String secretAccessKey;
    
    @Value("${asecapt.aws.region:${ASECAPT_AWS_REGION}}")
    private String region;
    
    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(region))
                .build();
    }
}
