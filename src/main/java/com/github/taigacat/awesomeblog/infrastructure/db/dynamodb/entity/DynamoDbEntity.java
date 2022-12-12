package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity;

import com.github.taigacat.awesomeblog.domain.common.Identified;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbConfiguration;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.KeyAttribute;
import io.micronaut.core.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

public interface DynamoDbEntity<T> extends Identified {

  @NonNull
  String getObjectName();

  @NonNull
  String getHashKey();

  @NonNull
  String getRangeKey();

  @NonNull
  DynamoDbTable<T> getTable(
      DynamoDbEnhancedClient enhancedClient,
      DynamoDbConfiguration dynamoDbConfiguration
  );

  default Integer getTtl() {
    return null;
  }

  default String createHashKeyValue(@NonNull List<KeyAttribute> keyAttributes) {
    List<KeyAttribute> keyAttributesWithObjectName = new ArrayList<>();
    keyAttributesWithObjectName.add(new KeyAttribute("object", this.getObjectName()));
    keyAttributesWithObjectName.addAll(keyAttributes);
    return this.createKeyValue(keyAttributesWithObjectName);
  }

  default String createRangeKeyValue(@NonNull List<KeyAttribute> keyAttributes) {
    if (keyAttributes.isEmpty()) {
      return " ";
    } else {
      return this.createKeyValue(keyAttributes);
    }
  }

  private String createKeyValue(List<KeyAttribute> keyAttributes) {
    return keyAttributes.stream()
        .map(KeyAttribute::join)
        .collect(Collectors.joining(""));
  }
}
