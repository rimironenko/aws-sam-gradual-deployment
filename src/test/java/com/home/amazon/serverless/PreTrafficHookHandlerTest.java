package com.home.amazon.serverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.LifecycleEventStatus;
import software.amazon.awssdk.services.codedeploy.model.PutLifecycleEventHookExecutionStatusRequest;
import software.amazon.awssdk.services.codedeploy.model.PutLifecycleEventHookExecutionStatusResponse;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PreTrafficHookHandlerTest {

    private static final String TEST_DEPLOYMENT_ID = "1";
    private static final String TEST_EXECUTION_ID = "2";

    @Mock
    private CodeDeployClient codeDeployClient;

    @Mock
    private Context context;

    @Mock
    private LambdaLogger logger;

    @Mock
    private PutLifecycleEventHookExecutionStatusResponse expectedResponse;

    private Map<String, String> testInput;

    @BeforeEach
    public void setUp() {
        testInput = new HashMap<>();
        testInput.put(PreTrafficHookHandler.DEPLOYMENT_ID_PARAM, TEST_DEPLOYMENT_ID);
        testInput.put(PreTrafficHookHandler.HOOK_EXECUTION_ID_PARAM, TEST_EXECUTION_ID);
        when(context.getLogger()).thenReturn(logger);
        when(codeDeployClient.putLifecycleEventHookExecutionStatus(any(PutLifecycleEventHookExecutionStatusRequest.class)))
                .thenReturn(expectedResponse);
        when(expectedResponse.lifecycleEventHookExecutionId()).thenReturn(TEST_EXECUTION_ID);
    }

    @Test
    public void shouldProceedWithDeploymentIfLifecycleIsSuccess() {
        try (MockedStatic<DependencyFactory> mockedStatic = mockStatic(DependencyFactory.class)) {
            when(DependencyFactory.codeDeployClient()).thenReturn(codeDeployClient);
            when(DependencyFactory.lifeCycleStatus()).thenReturn(LifecycleEventStatus.SUCCEEDED.toString());
            PreTrafficHookHandler target = new PreTrafficHookHandler();
            PutLifecycleEventHookExecutionStatusResponse actualResponse = target.handleRequest(testInput, context);
            assertEquals(TEST_EXECUTION_ID, actualResponse.lifecycleEventHookExecutionId());

        }
    }

}