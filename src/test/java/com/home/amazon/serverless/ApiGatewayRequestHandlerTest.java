package com.home.amazon.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApiGatewayRequestHandlerTest {

    private static final String TEST_TABLE_NAME = "TestTable";
    private static final String TEST_ID = "1";

    @Mock
    private DynamoDbEnhancedClient dynamoClient;

    @Mock
    private APIGatewayProxyRequestEvent request;

    @Mock
    private Context context;

    @Mock
    private DynamoDbTable<DataModel> table;

    @Test
    public void shouldReturnItemWhenExists() {
        when(dynamoClient.table(eq(TEST_TABLE_NAME), any(TableSchema.class))).thenReturn(table);
        DataModel testData = new DataModel();
        testData.setId(TEST_ID);
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put(DataModel.PARTITION_KEY, TEST_ID);
        when(request.getPathParameters()).thenReturn(pathParameters);
        Key testKey = Key.builder().partitionValue(TEST_ID).build();
        when(table.getItem(eq(testKey))).thenReturn(testData);
        try (MockedStatic<DependencyFactory> dependencyFactoryMockedStatic = mockStatic(DependencyFactory.class)) {
            when(DependencyFactory.dynamoDbEnhancedClient()).thenReturn(dynamoClient);
            when(DependencyFactory.tableName()).thenReturn(TEST_TABLE_NAME);
            ApiGatewayRequestHandler target = new ApiGatewayRequestHandler();
            APIGatewayProxyResponseEvent response = target.handleRequest(request, context);
            verify(table).getItem(eq(testKey));
            assertEquals(testData.toString(), response.getBody());
            assertEquals(ApiGatewayRequestHandler.STATUS_CODE_SUCCESS, response.getStatusCode());
        }
    }

    @Test
    public void shouldNotReturnItemWhenNotExists() {
        when(dynamoClient.table(eq(TEST_TABLE_NAME), any(TableSchema.class))).thenReturn(table);
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put(DataModel.PARTITION_KEY, TEST_ID);
        when(request.getPathParameters()).thenReturn(pathParameters);
        when(table.getItem(any(Key.class))).thenReturn(null);
        try (MockedStatic<DependencyFactory> dependencyFactoryMockedStatic = mockStatic(DependencyFactory.class)) {
            when(DependencyFactory.dynamoDbEnhancedClient()).thenReturn(dynamoClient);
            when(DependencyFactory.tableName()).thenReturn(TEST_TABLE_NAME);
            ApiGatewayRequestHandler target = new ApiGatewayRequestHandler();
            APIGatewayProxyResponseEvent response = target.handleRequest(request, context);
            verify(table).getItem(any(Key.class));
            assertTrue(response.getBody().isEmpty());
            assertEquals(ApiGatewayRequestHandler.STATUS_CODE_SUCCESS, response.getStatusCode());
        }
    }

    @Test
    public void shouldNotReturnItemWhenPathParameterIsAbsent() {
        when(request.getPathParameters()).thenReturn(Collections.emptyMap());
        try (MockedStatic<DependencyFactory> dependencyFactoryMockedStatic = mockStatic(DependencyFactory.class)) {
            when(DependencyFactory.dynamoDbEnhancedClient()).thenReturn(dynamoClient);
            when(DependencyFactory.tableName()).thenReturn(TEST_TABLE_NAME);
            ApiGatewayRequestHandler target = new ApiGatewayRequestHandler();
            APIGatewayProxyResponseEvent response = target.handleRequest(request, context);
            verifyNoInteractions(table);
            assertTrue(response.getBody().isEmpty());
            assertEquals(ApiGatewayRequestHandler.STATUS_CODE_SUCCESS, response.getStatusCode());
        }
    }

}