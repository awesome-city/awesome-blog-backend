package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity;

import com.github.taigacat.awesomeblog.domain.common.Identified;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbConfiguration;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.KeyAttribute;
import io.micronaut.core.annotation.NonNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

public interface DynamoDbEntity<T> extends Identified {

  String HASH_KEY = "hashKey";

  String RANGE_KEY = "rangeKey";

  @NonNull
  String getObjectName();

  @NonNull
  String getHashKey();

  default void setHashKey(String hashKey) {
  }

  @NonNull
  String getRangeKey();

  default void setRangeKey(String rangeKey) {
  }

  default Integer getTtl() {
    return null;
  }

  @NonNull
  DynamoDbTable<T> getTable(
      DynamoDbEnhancedClient enhancedClient,
      DynamoDbConfiguration dynamoDbConfiguration
  );

  default String createHashKeyValue(@NonNull KeyAttribute... keyAttributes) {
    List<KeyAttribute> keyAttributesWithObjectName = new ArrayList<>();
    keyAttributesWithObjectName.add(new KeyAttribute("object", this.getObjectName()));
    keyAttributesWithObjectName.addAll(Arrays.stream(keyAttributes).toList());
    return this.createKeyValue(keyAttributesWithObjectName);
  }

  default String createRangeKeyValue(@NonNull KeyAttribute... keyAttributes) {
    if (keyAttributes.length == 0) {
      return " ";
    } else {
      return this.createKeyValue(Arrays.stream(keyAttributes).toList());
    }
  }

  private String createKeyValue(List<KeyAttribute> keyAttributes) {
    return keyAttributes.stream()
        .map(KeyAttribute::join)
        .collect(Collectors.joining(""));
  }
}
