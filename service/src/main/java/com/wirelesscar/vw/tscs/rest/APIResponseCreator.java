package com.wirelesscar.vw.tscs.rest;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wirelesscar.vw.tscs.rest.model.APIErrorMessage;

import java.util.HashMap;
import java.util.Map;

public final class APIResponseCreator {

  private APIResponseCreator() {
  }

  public static APIGatewayProxyResponseEvent buildSuccessfulResponseEvent(int httpStatusCode, String messageBody) {
    APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put("Content-Type", "application/json");
    headerMap.put("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
    response.setHeaders(headerMap);
    response.setStatusCode(httpStatusCode);
    response.setBody(messageBody);
    return response;
  }

  public static APIGatewayProxyResponseEvent buildErrorResponse(int httpStatusCode, String errorMessage) {
    APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put("Content-Type", "application/problem+json");
    headerMap.put("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
    response.setHeaders(headerMap);
    response.setStatusCode(httpStatusCode);
    try {
      response.setBody(new APIErrorMessage(httpStatusCode, errorMessage).toJSON());
    } catch (JsonProcessingException jpe) {
      System.out.println("Error!!!");
    }
    return response;
  }
}
