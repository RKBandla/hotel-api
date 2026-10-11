package com.example.hotel_api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequest;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

/**
 * Entry point for AWS Lambda.
 *
 * API Gateway (HTTP API) receives the browser's request and passes it to Lambda as an event.
 * This class starts Spring Boot once (the "cold start") and hands every event to it, so the
 * SAME controllers, services and security rules run in Lambda as on your laptop.
 *
 * Lambda handler setting:  com.example.hotel_api.StreamLambdaHandler::handleRequest
 * (Locally you still run HotelApiApplication as usual.)
 */
public class StreamLambdaHandler implements RequestStreamHandler {

    private static final SpringBootLambdaContainerHandler<HttpApiV2ProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            // HTTP API (payload format 2.0) -> Spring Boot
            handler = SpringBootLambdaContainerHandler.getHttpApiV2ProxyHandler(HotelApiApplication.class);
        } catch (ContainerInitializationException e) {
            throw new RuntimeException("Could not start Spring Boot inside Lambda", e);
        }
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        handler.proxyStream(input, output, context);
    }
}