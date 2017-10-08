package com.traverse.data.cloud;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.traverse.data.Audio;
import com.traverse.data.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public class DBClient {

    private DynamoDB dynamoDB;
    private DynamoDBMapper mapper;
    private AmazonDynamoDB amazonDynamoDB;

    public DBClient(
            @Value("${cloud.aws_access_key_id}") String awsAccessKeyID,
            @Value("${cloud.aws_secret_access_key}") String awsSecretAccessKey,
            @Value("${cloud.dynamoDB_table_name_users}") String userTableName,
            @Value("${cloud.dynamoDB_table_name_audio}") String audioTableName,
            @Value("${cloud.dynamoDB_table_name_auth}") String authTableName) {

        amazonDynamoDB = AmazonDynamoDBClientBuilder
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
        dynamoDB = new DynamoDB(amazonDynamoDB);
        mapper = new DynamoDBMapper(amazonDynamoDB);

        createTable(userTableName, User.DB_IDENTIFIER_USER_ID, amazonDynamoDB, null);
        createAudioTable(audioTableName, amazonDynamoDB);

//        createTable(authTableName, AuthDatabase.DB_IDENTIFIER_TOKEN, amazonDynamoDB, "expiration");
        createTable(authTableName, AuthDatabase.DB_IDENTIFIER_TOKEN, amazonDynamoDB, null);
    }

    public DynamoDBMapper getMapper() {
        return mapper;
    }

    public DynamoDB getDynamoDB() {
        return dynamoDB;
    }

    public AmazonDynamoDB getAmazonDynamoDB() {
        return amazonDynamoDB;
    }

    /**
     *
     * @param tableName table name to create
     * @param partitionKey Name of partitionKey, content it describes must be string
     * @param amazonDynamoDB client instance.
     */
    private void createTable(String tableName, String partitionKey, AmazonDynamoDB amazonDynamoDB, String ttlAttribute) {
        System.out.println("Checking table: " + tableName);
        try {

            ArrayList<KeySchemaElement> keySchema = new ArrayList<>();
            keySchema.add(new KeySchemaElement()
                    .withAttributeName(partitionKey)
                    .withKeyType(KeyType.HASH));

            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();
            attributeDefinitions
                    .add(new AttributeDefinition()
                            .withAttributeName(partitionKey)
                            .withAttributeType(ScalarAttributeType.S));

            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withAttributeDefinitions(attributeDefinitions)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(2L)
                            .withWriteCapacityUnits(2L));

            if (TableUtils.createTableIfNotExists(amazonDynamoDB, request)){
                System.out.println("Initializing " + tableName + " for first time since none are found\nPlease wait...");
            } else {
                System.out.println("Table already exists.");
            }

            Table table = new DynamoDB(amazonDynamoDB).getTable(tableName);
            System.out.println("Waiting for " + tableName + " to be active...");
            table.waitForActive();

            if (ttlAttribute != null){
                UpdateTimeToLiveRequest timeToLiveRequest = new UpdateTimeToLiveRequest();
                timeToLiveRequest.setTableName(tableName);
                TimeToLiveSpecification ttlSpec = new TimeToLiveSpecification();
                ttlSpec.setAttributeName(ttlAttribute);
                ttlSpec.setEnabled(true);

                timeToLiveRequest.withTimeToLiveSpecification(ttlSpec);
                amazonDynamoDB.updateTimeToLive(timeToLiveRequest);
            }

        } catch (Exception e) {
            System.err.println("CreateTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }


    public void createAudioTable(String tableName, AmazonDynamoDB amazonDynamoDB){

        System.out.println("Checking table: " + tableName);
        try {

            ArrayList<KeySchemaElement> keySchema = new ArrayList<>();
            keySchema.add(new KeySchemaElement()
                    .withAttributeName(Audio.DB_IDENTIFIER_AUDIO_ID)
                    .withKeyType(KeyType.HASH));

            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();
            attributeDefinitions
                    .add(new AttributeDefinition()
                            .withAttributeName(Audio.DB_IDENTIFIER_AUDIO_ID)
                            .withAttributeType(ScalarAttributeType.S));
            attributeDefinitions
                    .add(new AttributeDefinition()
                            .withAttributeName(Audio.DB_IDENTIFIER_UPLOAD_DATE)
                            .withAttributeType(ScalarAttributeType.N));
            attributeDefinitions
                    .add(new AttributeDefinition()
                            .withAttributeName(Audio.DB_IDENTIFIER_UPLOAD_TIME)
                            .withAttributeType(ScalarAttributeType.N));

            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withAttributeDefinitions(attributeDefinitions)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(2L)
                            .withWriteCapacityUnits(2L));

            GlobalSecondaryIndex index = new GlobalSecondaryIndex()
                    .withIndexName(Audio.DB_IDENTIFIER_UPLOAD_DATE)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits((long) 1)
                            .withWriteCapacityUnits((long) 1))
                    .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

            ArrayList<KeySchemaElement> gsiKeySchema = new ArrayList<>();
            gsiKeySchema.add(new KeySchemaElement().withAttributeName(Audio.DB_IDENTIFIER_UPLOAD_DATE).withKeyType(KeyType.HASH));
            gsiKeySchema.add(new KeySchemaElement().withAttributeName(Audio.DB_IDENTIFIER_UPLOAD_TIME).withKeyType(KeyType.RANGE));
            index.setKeySchema(gsiKeySchema);
            request.withGlobalSecondaryIndexes(index);

            if (TableUtils.createTableIfNotExists(amazonDynamoDB, request)){
                System.out.println("Initializing " + tableName + " for first time since none are found\nPlease wait...");
            } else {
                System.out.println("Table already exists.");
            }

            Table table = new DynamoDB(amazonDynamoDB).getTable(tableName);
            System.out.println(table.describe());
            System.out.println("Waiting for " + tableName + " to be active...");
            table.waitForActive();
            System.out.println(table.getTableName() + " is ready!");

        } catch (Exception e) {
            System.err.println("CreateTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }
}
