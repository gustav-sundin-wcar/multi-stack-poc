package com.wirelesscar.vw.tscs.db;

import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.wirelesscar.vw.tscs.db.model.Certificate;

import java.util.*;

public class TrustStoreDbHelper {

  public static DynamoDbTestServer setupDB() {
    DynamoDbTestServer dynamoDbTestServer = new DynamoDbTestServer();

    dynamoDbTestServer.initializeDynamoDB();
    Table trustStoreContainersTable = dynamoDbTestServer.createTable("solution-environment_id-truststorecontainer-service-containers",
      Arrays.asList(new AttributeDefinition("regionBrandECU", ScalarAttributeType.S),
        new AttributeDefinition("versionId", ScalarAttributeType.N)),
      new KeySchemaElement("regionBrandECU", KeyType.HASH),
      new KeySchemaElement("versionId", KeyType.RANGE));
    Table trustStoresTable = dynamoDbTestServer.createTable("solution-environment_id-truststorecontainer-service-truststores",
      Arrays.asList(new AttributeDefinition("id", ScalarAttributeType.S)),
      new KeySchemaElement("id", KeyType.HASH));
    Table certificatesTable = dynamoDbTestServer.createTable("solution-environment_id-truststorecontainer-service-certificates",
      Arrays.asList(new AttributeDefinition("id", ScalarAttributeType.S)),
      new KeySchemaElement("id", KeyType.HASH));
    Table[] tables = {trustStoreContainersTable, trustStoresTable, certificatesTable};

    dynamoDbTestServer.createDatabaseSchema(tables);

    return dynamoDbTestServer;
  }

  public static Certificate populateCertificate(String certId, String certificate) {
    Certificate cert = new Certificate(certId, certificate);
    DatabaseHandler.getInstance().saveCertificate(cert);
    return cert;
  }
}
