package com.wirelesscar.vw.tscs.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamoDbRepository<T, K> implements Repository<T, K> {
  private final Class<T> entityClass;
  private AmazonDynamoDB client;
  private DynamoDBMapper dynamoDBMapper;

  private static DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression();

  private static final DynamoDBMapperConfig UPDATE_CONFIG = new DynamoDBMapperConfig.Builder()
    .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
    .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE).build();

  private static final DynamoDBMapperConfig SAVE_CONFIG = new DynamoDBMapperConfig.Builder()
    .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
    .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.PUT).build();

  /**
   * Initializes the generic Repository with DynamoDB Connection and the target
   * entity.
   *
   * @param client      DynamoDB Client Connection
   * @param entityClass The target entity for the repository
   */
  public DynamoDbRepository(AmazonDynamoDB client, Class<T> entityClass, String tableName) {
    this.entityClass = entityClass;

    this.client = client;
    TableNameOverride tableNameOverride = TableNameOverride
      .withTableNameReplacement(tableName);

    DynamoDBMapperConfig dynamoDBMapperConfig = new DynamoDBMapperConfig.Builder()
      .withTableNameOverride(tableNameOverride).build();

    this.dynamoDBMapper = new DynamoDBMapper(this.client, dynamoDBMapperConfig);
  }

  private void save(T entity, DynamoDBMapperConfig dynamoDBMapperConfig) {
    getMapper().save(entity, dynamoDBMapperConfig);
  }

  public void save(T entity) {
    save(entity, SAVE_CONFIG);
  }

  public void update(T entity) {
    save(entity, UPDATE_CONFIG);
  }

  public T findByKey(K key) {
    return getMapper().load(entityClass, key);
  }

  public T findByKeys(K hashKey, K rangeKey) {
    return getMapper().load(entityClass, hashKey, rangeKey);
  }

  public List<T> findByQuery(DynamoDBQueryExpression<T> expression) {
    return getMapper().query(entityClass, expression);
  }

  public List<T> queryByIndex(String indexName, String hashKeyAttributeName, String rangeKeyAttributeName, K hashKey,
                              K rangeKey) {
    Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
    expressionAttributeValues.put(":hk", new AttributeValue().withS(hashKey.toString()));
    expressionAttributeValues.put(":rk", new AttributeValue().withS(rangeKey.toString()));

    DynamoDBQueryExpression<T> expression = new DynamoDBQueryExpression<T>().withConsistentRead(false)
      .withIndexName(indexName)
      .withKeyConditionExpression(hashKeyAttributeName + " = :hk and " + rangeKeyAttributeName + " = :rk")
      .withExpressionAttributeValues(expressionAttributeValues);

    return getMapper().query(entityClass, expression);
  }

  public PaginatedScanList<T> findAll(Class<T> clazz) {
    return getMapper().scan(clazz, dynamoDBScanExpression);
  }

  public PaginatedScanList<T> findSome(Class<T> clazz, DynamoDBScanExpression dynamoDBScanExpression) {
    return getMapper().scan(clazz, dynamoDBScanExpression);
  }

  public boolean exists(K key) {
    return findByKey(key) != null;
  }

  public void delete(K hashKey, K rangeKey) {
    T entity = this.findByKeys(hashKey, rangeKey);
    if (entity != null) {
      getMapper().delete(entity);
    } else {
      // TODO: raise error that entity does not exist
    }
  }

  public void delete(K key) {
    T entity = this.findByKey(key);
    if (entity != null) {
      getMapper().delete(entity);
    } else {
      // TODO: raise error that the entity does not exist
    }
  }

  public DynamoDBMapper getMapper() {
    return this.dynamoDBMapper;
  }

  protected AmazonDynamoDB getClient() {
    return this.client;
  }
}
