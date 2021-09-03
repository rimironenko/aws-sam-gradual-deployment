package com.home.amazon.serverless;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.LifecycleEventStatus;
import software.amazon.awssdk.services.codedeploy.model.PutLifecycleEventHookExecutionStatusRequest;
import software.amazon.awssdk.services.codedeploy.model.PutLifecycleEventHookExecutionStatusResponse;

import java.util.Map;

public class PreTrafficHookHandler implements RequestHandler<Map<String, String>, PutLifecycleEventHookExecutionStatusResponse> {

    static final String DEPLOYMENT_ID_PARAM = "DeploymentId";
    static final String HOOK_EXECUTION_ID_PARAM = "LifecycleEventHookExecutionId";
    private static final String NEW_VERSION_ENV_NAME = "NewVersion";
    private static final String LIFECYCLE_STATUS_ENV_NAME = "LifecycleStatus";

    private final CodeDeployClient codeDeployClient;

    public PreTrafficHookHandler() {
        codeDeployClient = DependencyFactory.codeDeployClient();
    }

    @Override
    public PutLifecycleEventHookExecutionStatusResponse handleRequest(Map<String, String> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Testing the pipeline");

        //The IDs below are initialized by a CodeDeploy execution
        final String deploymentId = input.get(DEPLOYMENT_ID_PARAM);
        final String lifecycleEventHookExecutionId = input.get(HOOK_EXECUTION_ID_PARAM);

        //New version of the {@link ApiGatewayRequestHandler} Lambda function to use it in the testing if needed.
        String newLambdaFunctionVersion = System.getenv(NEW_VERSION_ENV_NAME);
        logger.log("New ApiGatewayRequestHandler version: " + newLambdaFunctionVersion);

        /* Lifecycle execution status for the demo ij just taken from the Environment variable.
           It can be any operation to ensure that the traffic shifting can be started.
        */
        LifecycleEventStatus lifecycleEventStatus = LifecycleEventStatus.fromValue(System.getenv(LIFECYCLE_STATUS_ENV_NAME));
        PutLifecycleEventHookExecutionStatusRequest newRequest = PutLifecycleEventHookExecutionStatusRequest.builder()
                .deploymentId(deploymentId)
                .lifecycleEventHookExecutionId(lifecycleEventHookExecutionId)
                .status(lifecycleEventStatus)
                .build();

        return codeDeployClient.putLifecycleEventHookExecutionStatus(newRequest);
    }
}
