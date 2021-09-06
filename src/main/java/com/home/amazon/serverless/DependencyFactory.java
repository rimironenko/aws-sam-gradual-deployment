
package com.home.amazon.serverless;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * The module containing all dependencies required by the {@link ApiGatewayRequestHandler} and {@link PreTrafficHookHandler}.
 */
public class DependencyFactory {

    private static final String ENV_VARIABLE_TABLE = "TABLE";
    private static final String LIFECYCLE_STATUS_ENV_NAME = "LifecycleStatus";

    private DependencyFactory() {}

    /**
     * @return an instance of DynamoDbClient
     */
    public static DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(DynamoDbClient.builder()
                        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                        .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                        .httpClientBuilder(UrlConnectionHttpClient.builder())
                        .build())
                .build();
    }

    public static String tableName() {
        return System.getenv(ENV_VARIABLE_TABLE);
    }

    public static CodeDeployClient codeDeployClient() {
        return CodeDeployClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build();
    }

    public static String lifeCycleStatus() {
        return System.getenv(LIFECYCLE_STATUS_ENV_NAME);
    }
}
