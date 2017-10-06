package com.traverse.data.cloud;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class DBClient {

    private DynamoDB dynamoDB;
    private DynamoDBMapper mapper;
    private AmazonDynamoDB client;

    public DBClient(@Value("${cloud.aws_access_key_id}") String awsAccessKeyID,
                        @Value("${cloud.aws_secret_access_key}") String awsSecretAccessKey) {
        client = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(new AWSCredentials() {
                    @Override
                    public String getAWSAccessKeyId() {
                        return awsAccessKeyID;
                    }

                    @Override
                    public String getAWSSecretKey() {
                        return awsSecretAccessKey;
                    }
                }))
                .build();
        dynamoDB = new DynamoDB(client);
        mapper = new DynamoDBMapper(client);
    }

    public DynamoDBMapper getMapper() {
        return mapper;
    }

    public DynamoDB getDynamoDB() {
        return dynamoDB;
    }

    public AmazonDynamoDB getClient() {
        return client;
    }

}
