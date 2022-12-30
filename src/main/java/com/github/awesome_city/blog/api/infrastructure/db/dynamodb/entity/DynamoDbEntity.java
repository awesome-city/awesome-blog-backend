package com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity;

import com.github.awesome_city.blog.api.domain.common.Identified;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.common.DynamoDbTableType;
import io.micronaut.core.annotation.NonNull;

public interface DynamoDbEntity extends Identified {

  @NonNull
  DynamoDbTableType getTableType();

  @NonNull
  String getHashKey();

  void setHashKey(String hashKey);

  @NonNull
  String getRangeKey();

  void setRangeKey(String rangeKey);

  default Integer getTtl() {
    return null;
  }
}
