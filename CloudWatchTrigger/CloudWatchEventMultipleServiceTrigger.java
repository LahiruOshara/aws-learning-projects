public class CloudWatchEvent {
    public static void main(String[] args) {
        String ruleName = "schedule";
        String scheduleExpression = "some schedule";

        AmazonCloudWatchEvents cwe = AmazonCloudWatchEventsClientBuilder.defaultClient();

        // create CloudWatch Event rule
        createRule(cwe, ruleName, scheduleExpression);

        // create permission to allow Lambda function to be invoked by CloudWatch Event rule
        AWSLambda lambda = AWSLambdaClientBuilder.standard().build();
        addLambdaPermission(lambda, ruleName, "LambdaFunctionName1");
        addLambdaPermission(lambda, ruleName, "LambdaFunctionName2");

        // create lifecycle hook for an Auto Scaling group
        AmazonAutoScaling asg = AmazonAutoScalingClientBuilder.defaultClient();
        createLifecycleHook(asg, "AutoScalingGroupName", "LifecycleHookName", "arn:aws:sns:REGION:ACCOUNT-ID:TopicName");

        // create CloudWatch Event target for Lambda function and Auto Scaling group
        PutTargetsRequest targetsRequest = new PutTargetsRequest()
                .withRule(ruleName)
                .withTargets(
                        createLambdaTarget("LambdaFunctionName1"),
                        createLambdaTarget("LambdaFunctionName2"),
                        createAsgTarget("AutoScalingGroupName", "LifecycleHookName")
                );
        cwe.putTargets(targetsRequest);
    }

    private static void createRule(AmazonCloudWatchEvents cwe, String ruleName, String scheduleExpression) {
        PutRuleRequest request = new PutRuleRequest()
                .withName(ruleName)
                .withScheduleExpression(scheduleExpression)
                .withDescription("Some description");
        cwe.putRule(request);
    }

    private static void addLambdaPermission(AWSLambda lambda, String ruleName, String functionName) {
        AddPermissionRequest request = new AddPermissionRequest()
                .withFunctionName(functionName)
                .withStatementId(ruleName)
                .withAction("lambda:InvokeFunction")
                .withPrincipal("events.amazonaws.com")
                .withSourceArn("arn:aws:events:REGION:ACCOUNT-ID:rule/" + ruleName);
        lambda.addPermission(request);
    }

    private static void createLifecycleHook(AmazonAutoScaling asg, String asgName, String hookName, String topicArn) {
        PutLifecycleHookRequest request = new PutLifecycleHookRequest()
                .withAutoScalingGroupName(asgName)
                .withLifecycleHookName(hookName)
                .withLifecycleTransition("autoscaling:EC2_INSTANCE_TERMINATING")
                .withDefaultResult("CONTINUE")
                .withHeartbeatTimeout(3600)
                .withNotificationTargetARN(topicArn);
        asg.putLifecycleHook(request);
    }

    private static Target createLambdaTarget(String functionName) {
        return new Target()
                .withId(functionName)
                .withArn("arn:aws:lambda:REGION:ACCOUNT-ID:function:" + functionName);
    }

    private static Target createAsgTarget(String asgName, String hookName) {
        return new Target()
                .withId(asgName)
                .withArn("arn:aws:autoscaling:REGION:ACCOUNT-ID:autoScalingGroup:" + asgName + ":lifecycleHook/" + hookName);
    }
}
