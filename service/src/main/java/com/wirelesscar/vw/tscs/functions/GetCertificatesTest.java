package com.wirelesscar.vw.tscs.functions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelesscar.vw.tscs.db.DatabaseHandler;
import com.wirelesscar.vw.tscs.db.DynamoDbTestServer;
import com.wirelesscar.vw.tscs.db.TrustStoreDbHelper;
import com.wirelesscar.vw.tscs.rest.model.ResponseCertificate;
import com.wirelesscar.vw.tscs.rest.model.ResponseCertificates;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetCertificatesTest {
  private GetCertificates getCertificates;
  private long randomness;
  private Context contextMock;

  private String exampleCertificatePayload = "-----BEGIN CERTIFICATE-----\n" +
    "MIICrjCCAlWgAwIBAgILdtghibKVu6vtXSIwCgYIKoZIzj0EAwIwRDELMAkGA1UE\n" +
    "BhMCREUxDTALBgNVBAoMBFZXQUcxJjAkBgNVBAMMHVZXR19QUkRDVF9ERVZMX1FV\n" +
    "QV9DVUlEQ0FfMTAyMB4XDTE5MDQwOTE3NTEzNFoXDTI0MDQwOTE3NTEzNFowPTEL\n" +
    "MAkGA1UEBhMCREUxDTALBgNVBAoMBFZXQUcxHzAdBgNVBAMMFkJWV1RETUUxWkw0\n" +
    "MDAwMDEzOjAwNzUwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAASQ/nGopAH4putd\n" +
    "+c+Gm6qElWUjY/z35btPZJipdvhiIoIrf+DMLsqZd0/SCmM7I8t9WiBd0cUOrwr7\n" +
    "xiQjHgxYo4IBMzCCAS8wHQYDVR0OBBYEFIBop2dYdyyNiInw6BeQQkf4SUduMB8G\n" +
    "A1UdIwQYMBaAFC2DGmvvl6UJylAQVtWV3QD9ApLLMAwGA1UdEwEB/wQCMAAwDgYD\n" +
    "VR0PAQH/BAQDAgPIMB0GA1UdJQQWMBQGCCsGAQUFBwMCBggrBgEFBQcDATBPBgNV\n" +
    "HR8ESDBGMESgQqBAhj5odHRwOi8vcWFjcmwudndnLWNvbm5lY3QuY29tL1ZXR19Q\n" +
    "UkRDVF9ERVZMX1FVQV9DVUlEQ0FfMTAyLmNybDA+BggrBgEFBQcBAQQyMDAwLgYI\n" +
    "KwYBBQUHMAGGImh0dHA6Ly9lY3UucWFvY3NwLnZ3Zy1jb25uZWN0LmNvbS8wHwYD\n" +
    "VR0gBBgwFjAUBhIrBgEEAZkKvkkUAgICAQACAgEwCgYIKoZIzj0EAwIDRwAwRAIg\n" +
    "QO78npCG8DaSDIuWH/vPg7i9lEZO5bYn+tWzXf37nwsCIE4SpNM39sYq22py202T\n" +
    "A3rNIDRcBhNLDe2k9rTRtXk8\n" +
    "-----END CERTIFICATE-----\n";

  @Before
  public void setup() {
    System.setProperty("aws.region", "eu-west-1");

    randomness = System.nanoTime();

    contextMock = Mockito.mock(Context.class);
    Mockito.when(contextMock.getFunctionName()).thenReturn("TestFunctionName" + randomness);

    DynamoDbTestServer dynamoDbTestServer = TrustStoreDbHelper.setupDB();
    DatabaseHandler.getInstance().setDynamoDBClient(dynamoDbTestServer.getAmazonDynamoDB());

    getCertificates = new GetCertificates();
  }

  @Test
  public void shouldGetListOfAllCertificates() throws JsonProcessingException {
    APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();

    TrustStoreDbHelper.populateCertificate("certificateId2", exampleCertificatePayload);
    TrustStoreDbHelper.populateCertificate("certificateId1", exampleCertificatePayload);

    APIGatewayProxyResponseEvent response = getCertificates.handleRequest(request, contextMock);

    Assert.assertEquals(200, response.getStatusCode().intValue());
    Assert.assertNotNull(response.getBody());

    ResponseCertificates responseData = getResponseCertificates(response);
    List<ResponseCertificate> responseCertificateList = responseData.getCertficates();
    Assert.assertEquals(2, responseCertificateList.size());
  }

  @Test
  public void shouldReturnEmptyList() throws JsonProcessingException {
    APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();

    APIGatewayProxyResponseEvent response = getCertificates.handleRequest(request, contextMock);

    Assert.assertEquals(200, response.getStatusCode().intValue());
    Assert.assertNotNull(response.getBody());

    ResponseCertificates responseData = getResponseCertificates(response);
    List<ResponseCertificate> responseCertificateList = responseData.getCertficates();
    Assert.assertTrue(responseCertificateList.isEmpty());
  }

  private ResponseCertificates getResponseCertificates(APIGatewayProxyResponseEvent response) throws JsonProcessingException {
    return new ObjectMapper().readValue(response.getBody(), ResponseCertificates.class);
  }
}
