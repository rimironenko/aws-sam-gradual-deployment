package com.home.amazon.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Collections;
import java.util.Map;

public class ApiGatewayRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static final Integer STATUS_CODE_SUCCESS = 200;

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final String dynamoDbTableName;
    private final TableSchema<DataModel> dynamoDbTableSchema;

    public ApiGatewayRequestHandler() {
        dynamoDbEnhancedClient = DependencyFactory.dynamoDbEnhancedClient();
        dynamoDbTableName = DependencyFactory.tableName();
        dynamoDbTableSchema = TableSchema.fromBean(DataModel.class);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        Map<String, String> pathParameters = input.getPathParameters();
        String response = "";
        if (pathParameters != null) {
            String itemPartitionKey = pathParameters.get(DataModel.PARTITION_KEY);
            if (itemPartitionKey != null && !itemPartitionKey.isEmpty()) {
                DynamoDbTable<DataModel> table = dynamoDbEnhancedClient.table(dynamoDbTableName, dynamoDbTableSchema);
                DataModel item = table.getItem(Key.builder().partitionValue(itemPartitionKey).build());
                if (item != null) {
                    response = item.toString();
                }
            }
        }
        return new APIGatewayProxyResponseEvent().withStatusCode(STATUS_CODE_SUCCESS)
                .withIsBase64Encoded(Boolean.FALSE)
                .withHeaders(Collections.emptyMap())
                .withBody(response);
    }
}
