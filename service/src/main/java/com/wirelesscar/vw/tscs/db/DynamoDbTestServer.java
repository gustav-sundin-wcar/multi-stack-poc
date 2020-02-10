package com.wirelesscar.vw.tscs.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static org.apache.logging.log4j.core.config.LoggerConfig.ROOT;

public class DynamoDbTestServer {

  static {
    System.setProperty("sqlite4java.library.path", "target/native-libs");
  }

  private AmazonDynamoDB amazonDynamoDB;

  public DynamoDbTestServer() {
  }

  public void initializeDynamoDB() {
    amazonDynamoDB = DynamoDBEmbedded.create().amazonDynamoDB();
  }

  /**
   * @param name
   * @param attributes
   * @param keys
   * @return
   */
  public Table createTable(String name, List<AttributeDefinition> attributes, KeySchemaElement... keys) {
    CreateTableRequest request = new CreateTableRequest().withTableName(name).withKeySchema(keys)
      .withAttributeDefinitions(attributes)
      .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(5L));

    CreateTableResult result = amazonDynamoDB.createTable(request);
    return new Table(amazonDynamoDB, request.getTableName(), result.getTableDescription());
  }

  public AmazonDynamoDB getAmazonDynamoDB() {
    return amazonDynamoDB;
  }

  public void createDatabaseSchema(Table[] tables) {
    Arrays.stream(tables).forEach(DynamoDbTestServer::waitForActive);
  }

  private static TableDescription waitForActive(Table table) {
    try {
      return table.waitForActive();
    } catch (InterruptedException e) {
      throw new RuntimeException(format(ROOT, "Unable to activate table: %s: %s", table, e.getMessage()), e);
    }
  }

}
