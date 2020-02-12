package com.wirelesscar.vw.tscs.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.google.common.annotations.VisibleForTesting;
import com.wirelesscar.vw.tscs.db.model.Certificate;
import javax.annotation.Nullable;
import java.util.List;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;

public final class DatabaseHandler {
  private AmazonDynamoDB dynamoDBClient;

  private static final String TABLE_BASE_NAME = "multi-stack-poc-truststorecontainer-service-";
  private static final String CERTIFICATE_TABLE_NAME = TABLE_BASE_NAME + "certificates";
  private static final int DRAFT_VERSION = 0;

  private DynamoDbRepository<Certificate, Object> certificateRepository;

  private static DatabaseHandler instance;

  public static DatabaseHandler getInstance() {
    if (instance == null) {
      instance = new DatabaseHandler();
    }
    return instance;
  }

  private DatabaseHandler() {
    if (dynamoDBClient == null) {
      dynamoDBClient = AmazonDynamoDBClientBuilder.standard().withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder())).build();
      System.out.println("Successfully initiated dynamoDBClient");
    }
    setDynamoDBClient(dynamoDBClient);
  }

  public void saveCertificate(Certificate certificate) {
    certificateRepository.save(certificate);
  }

  public @Nullable
  Certificate findCertificate(String id) {
    return certificateRepository.findByKey(id);
  }

  public List<Certificate> findAllCertificates() {
    System.out.println("Find all certificates");
    return certificateRepository.findAll(Certificate.class);
  }

  @VisibleForTesting
  public void setDynamoDBClient(AmazonDynamoDB dynamoDBClient) {
    System.out.println("Successfully overwrote dynamoDbClient");
    this.dynamoDBClient = dynamoDBClient;
    this.certificateRepository = new DynamoDbRepository<>(dynamoDBClient, Certificate.class, CERTIFICATE_TABLE_NAME);
  }

  @VisibleForTesting
  public AmazonDynamoDB getDynamoDBClient() {
    return this.dynamoDBClient;
  }

}
