package com.wirelesscar.vw.tscs.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.google.common.annotations.VisibleForTesting;
import com.wirelesscar.vw.tscs.db.model.Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

public final class DatabaseHandler {
  private AmazonDynamoDB dynamoDBClient;
  private final Logger LOG = LoggerFactory.getLogger(DatabaseHandler.class);

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
      dynamoDBClient = AmazonDynamoDBClientBuilder.standard().build();
      LOG.debug("Successfully initiated dynamoDBClient");
    }
    setDynamoDBClient(dynamoDBClient);
  }

  public void saveCertificate(Certificate certificate) {
    LOG.debug("saveCertificate: certificate {}", certificate);
    certificateRepository.save(certificate);
  }

  public @Nullable
  Certificate findCertificate(String id) {
    return certificateRepository.findByKey(id);
  }

  public List<Certificate> findAllCertificates() {
    return certificateRepository.findAll(Certificate.class);
  }

  @VisibleForTesting
  public void setDynamoDBClient(AmazonDynamoDB dynamoDBClient) {
    LOG.debug("Successfully overwrote dynamoDbClient");
    this.dynamoDBClient = dynamoDBClient;
    this.certificateRepository = new DynamoDbRepository<>(dynamoDBClient, Certificate.class, CERTIFICATE_TABLE_NAME);
  }

  @VisibleForTesting
  public AmazonDynamoDB getDynamoDBClient() {
    return this.dynamoDBClient;
  }

}
