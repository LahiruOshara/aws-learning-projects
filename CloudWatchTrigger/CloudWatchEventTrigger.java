import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEvents;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClientBuilder;
import com.amazonaws.services.cloudwatchevents.model.PutRuleRequest;
import com.amazonaws.services.cloudwatchevents.model.PutTargetsRequest;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.AddPermissionRequest;

public class CloudWatchEvent {
    public static void main(String[] args) {
        String functionName = "LambdaFunctionName";
        String ruleName = "TwoMonthEvent";
        String scheduleExpression = "some schedule"

        // create CloudWatch Event rule
        AmazonCloudWatchEvents cloudWatchEvents = AmazonCloudWatchEventsClientBuilder.defaultClient();
        PutRuleRequest putRuleRequest = new PutRuleRequest()
                .withName(ruleName)
                .withScheduleExpression(scheduleExpression)
                .withDescription(description);
        cloudWatchEvents.putRule(putRuleRequest);

//        // add permission to Lambda function
//        AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard();
//        builder.build().addPermission(new AddPermissionRequest()
//                .withFunctionName(functionName)
//                .withStatementId(ruleName)
//                .withAction("lambda:InvokeFunction")
//                .withPrincipal("events.amazonaws.com")
//                .withSourceArn("arn:aws:events:REGION:ACCOUNT-ID:rule/" + ruleName));

        // create CloudWatch Event target
        PutTargetsRequest putTargetsRequest = new PutTargetsRequest()
                .withRule(ruleName)
                .withTargets(
                        new Target().withId("1").withArn("arn:aws:lambda:REGION:ACCOUNT-ID:function:" + functionName));
        cloudWatchEvents.putTargets(putTargetsRequest);
    }
}
