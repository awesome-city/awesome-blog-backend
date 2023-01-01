package com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity;

import com.github.awesome_city.blog.api.domain.common.Identified;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.common.DynamoDbTableType;
import io.micronaut.core.annotation.NonNull;

public interface DynamoDbEntity extends Identified {

  @NonNull
  DynamoDbTableType tableType();

  @NonNull
  String getHashKey();

  @NonNull
  String getRangeKey();

  default Integer getTtl() {
    return null;
  }

  void setHashKey(String hashKey);

  void setRangeKey(String rangeKey);
}
