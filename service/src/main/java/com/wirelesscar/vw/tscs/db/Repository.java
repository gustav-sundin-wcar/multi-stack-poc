package com.wirelesscar.vw.tscs.db;

import java.util.List;

public interface Repository<T, K> {
  void save(T entity);

  void update(T entity);

  T findByKey(K key);

  T findByKeys(K hashKey, K rangeKey);

  List<T> queryByIndex(String indexName, String hashKeyAttributeName, String rangeKeyAttributeName, K hashKey, K rangeKey);

  boolean exists(K key);

  void delete(K key);

  void delete(K hashKey, K rangeKey);

}
