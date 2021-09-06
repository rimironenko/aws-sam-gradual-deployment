package com.home.amazon.serverless;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class DataModel {

    public static final String PARTITION_KEY = "id";

    private static final String JSON_TEMPLATE = "{\"id\":\"%s\"}";

    private String id;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format(JSON_TEMPLATE, id);
    }
}
