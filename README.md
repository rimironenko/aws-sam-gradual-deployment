# App

This project contains an AWS SAM application with [AWS Java SDK 2.x](https://github.com/aws/aws-sdk-java-v2) dependencies.
The application uses AWS SAM capability for a gradual deployment of Lambda functions and AWS SAM pipeline triggered by GitHub actions.
The infrastructure is present on the picture given below.
![image](https://miro.medium.com/max/1400/1*mNjTQlNiQBBYbyO-xmQDNg.png)

Please read more about this in my [Medium blog post](https://medium.com/@rostyslav.myronenko/gradual-deployment-of-aws-sam-java-application-6b932c781eae).

## Prerequisites
- Java 1.8+
- Apache Maven
- [AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
- Docker

## Development

The generated function handler class just returns the input. The configured AWS Java SDK client is created in `DependencyFactory` class and you can 
add the code to interact with the SDK client based on your use case.

#### Building the project
```
sam build
```

#### Testing it locally
```
sam local invoke
```

## Deployment

The generated project contains a default [SAM template](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/sam-resource-function.html) file `template.yaml` where you can 
configure different properties of your lambda function such as memory size and timeout. You might also need to add specific policies to the lambda function
so that it can access other AWS resources.

To deploy the application, you can run the following command:

```
sam deploy --guided
```

See [Deploying Serverless Applications](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-deploying.html) for more info.

## Traffic shifting after a deployment
[PreTrafficHookHandler Lambda function](src/main/java/com/home/amazon/serverless/PreTrafficHookHandler.java) validates the inftasrtucture before starting the traffic shifting. It takes the lifecycle status from the environemnt configuration, but can execute any custom business logic to decide if the traffic shifting can be started for a new Lambda version.

[SAM template](template.yml) contains "DeploymentPreference" property for [API Gateway Lambda handler](src/main/java/com/home/amazon/serverless/ApiGatewayRequestHandler.java) that starts the traffic shifting as configured if the PreTrafficHookHandler reports success.
