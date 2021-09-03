package com.home.amazon.serverless;


import com.amazonaws.services.lambda.runtime.Context;
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

    private final CodeDeployClient codeDeployClient;

    public PreTrafficHookHandler() {
        codeDeployClient = DependencyFactory.codeDeployClient();
    }

    @Override
    public PutLifecycleEventHookExecutionStatusResponse handleRequest(Map<String, String> input, Context context) {
        //The IDs below are initialized by a CodeDeploy execution
        final String deploymentId = input.get(DEPLOYMENT_ID_PARAM);
        final String lifecycleEventHookExecutionId = input.get(HOOK_EXECUTION_ID_PARAM);

        //
        String functionToTest = System.getenv(NEW_VERSION_ENV_NAME);

        LifecycleEventStatus lifecycleEventStatus = getLifecycleEventStatus(functionToTest);
        PutLifecycleEventHookExecutionStatusRequest newRequest = PutLifecycleEventHookExecutionStatusRequest.builder()
                .deploymentId(deploymentId)
                .lifecycleEventHookExecutionId(lifecycleEventHookExecutionId)
                .status(lifecycleEventStatus)
                .build();

        PutLifecycleEventHookExecutionStatusResponse executionStatusResponse =
                codeDeployClient.putLifecycleEventHookExecutionStatus(newRequest);

        return executionStatusResponse;
    }

    private LifecycleEventStatus getLifecycleEventStatus(String functionToTest) {
        return LifecycleEventStatus.SUCCEEDED;
    }
}
