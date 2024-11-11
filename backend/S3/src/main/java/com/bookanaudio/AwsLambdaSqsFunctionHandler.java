/*package com.bookanaudio;

import org.springframework.beans.factory.annotation.Autowired;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class AwsLambdaSqsFunctionHandler implements RequestHandler<SQSEvent, Void> {

    @Autowired
    private SqsEventProcessor sqsEventProcessor;

    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        sqsEventProcessor.processEvent(input);
        return null;
    }
}
*/