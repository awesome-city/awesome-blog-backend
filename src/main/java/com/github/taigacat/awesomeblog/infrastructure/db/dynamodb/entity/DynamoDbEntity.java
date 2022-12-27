package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity;

import com.github.taigacat.awesomeblog.domain.common.Identified;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbTableType;
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
