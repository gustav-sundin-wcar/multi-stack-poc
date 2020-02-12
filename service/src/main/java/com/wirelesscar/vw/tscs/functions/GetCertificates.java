package com.wirelesscar.vw.tscs.functions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelesscar.vw.tscs.db.DatabaseHandler;
import com.wirelesscar.vw.tscs.rest.model.ResponseCertificate;
import com.wirelesscar.vw.tscs.rest.APIResponseCreator;
import com.wirelesscar.vw.tscs.rest.model.ResponseCertificates;
import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetCertificates implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  private static final String LAMBDA_NAME = "GetCertificates";

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
    System.out.println("Start executing lamdba" + LAMBDA_NAME);
    APIGatewayProxyResponseEvent response;
    try {
      response = getCertificates(context);
    } catch (Exception e) {
      response = handleError(e, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    return response;
  }

  private APIGatewayProxyResponseEvent getCertificates(Context context) {

    List<ResponseCertificate> certificates = getCertificatesFromDb();

    try {
      APIGatewayProxyResponseEvent response = APIResponseCreator.buildSuccessfulResponseEvent(HttpStatus.SC_OK, new ResponseCertificates(certificates).toJSON());
      return response;
    } catch (JsonProcessingException e) {
      System.out.println(e.getMessage());
      return handleError(e, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private List<ResponseCertificate> getCertificatesFromDb() {
    System.out.println("Trying to get certificates from db");
    return DatabaseHandler.getInstance().findAllCertificates().stream().map(ResponseCertificate::new).collect(Collectors.toList());
  }

  private APIGatewayProxyResponseEvent handleError(Exception e, int httpStatus) {

    return APIResponseCreator.buildErrorResponse(httpStatus, e.getMessage());
  }

  public String toJSON(List<ResponseCertificate> certificates) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(certificates);
  }
}
