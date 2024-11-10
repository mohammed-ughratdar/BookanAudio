package com.bookanaudio;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class SQSHandler implements RequestHandler<SQSEvent, String> {

    @Autowired
    private ProcessBookPagesLambda processBookPagesLambda;

    @Override
    public String handleRequest(SQSEvent event, Context context) {
       
        return processBookPagesLambda.processSQSEvent(event);
    }
}
