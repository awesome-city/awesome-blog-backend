package com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity;

import com.github.awesome_city.blog.api.domain.common.Identified;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.common.DynamoDbTableType;
import io.micronaut.core.annotation.NonNull;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

public interface DynamoDbEntity extends Identified {

  @NonNull
  DynamoDbTableType tableType();

  @NonNull
  @DynamoDbPartitionKey
  String getHashKey();

  @NonNull
  @DynamoDbSortKey
  String getRangeKey();

  default Integer getTtl() {
    return null;
  }

  void setHashKey(String hashKey);

  void setRangeKey(String rangeKey);
}
