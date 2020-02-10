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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class GetCertificates implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
  private static final Logger LOG = LoggerFactory.getLogger(GetCertificates.class);

  private static final String LAMBDA_NAME = "GetCertificates";

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
    LOG.debug("Starting executing {} Lambda", LAMBDA_NAME);

    APIGatewayProxyResponseEvent response = getCertificates(context);

    return response;
  }

  private APIGatewayProxyResponseEvent getCertificates(Context context) {

    List<ResponseCertificate> certificates = getCertificatesFromDb();

    try {
      APIGatewayProxyResponseEvent response = APIResponseCreator.buildSuccessfulResponseEvent(HttpStatus.SC_OK, new ResponseCertificates(certificates).toJSON());
      LOG.debug("Successfully executed {}} Lambda", LAMBDA_NAME);
      return response;
    } catch (JsonProcessingException e) {
      return handleError(e, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private List<ResponseCertificate> getCertificatesFromDb() {
    return DatabaseHandler.getInstance().findAllCertificates().stream().map(ResponseCertificate::new).collect(Collectors.toList());
  }

  private APIGatewayProxyResponseEvent handleError(Exception e, int httpStatus) {
    LOG.error("Got exception in {} {}", LAMBDA_NAME, e.getMessage(), e);

    return APIResponseCreator.buildErrorResponse(httpStatus, e.getMessage());
  }

  public String toJSON(List<ResponseCertificate> certificates) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(certificates);
  }
}
