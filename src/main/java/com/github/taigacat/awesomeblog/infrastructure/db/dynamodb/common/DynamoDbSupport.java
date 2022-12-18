package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common;

import io.micronaut.core.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class DynamoDbSupport {

  public static String createHashKeyValue(String objectName,
      @NonNull String... keyAttributes) {
    List<String> keyAttributesWithObjectName = new ArrayList<>();
    keyAttributesWithObjectName.addAll(List.of("object", objectName));
    keyAttributesWithObjectName.addAll(List.of(keyAttributes));
    return createKeyValue(keyAttributesWithObjectName);
  }

  public static String createRangeKeyValue(@NonNull String... keyAttributes) {
    if (keyAttributes.length == 0) {
      return " ";
    } else {
      return createKeyValue(List.of(keyAttributes));
    }
  }

  public static String createKeyValue(List<String> keyAttributes) {
    if (keyAttributes == null || keyAttributes.size() % 2 == 1) {
      throw new IllegalArgumentException();
    }

    StringBuilder keyValue = new StringBuilder();
    for (int i = 0; i < keyAttributes.size(); i = i + 2) {
      final String k = keyAttributes.get(i);
      final String v = keyAttributes.get(i + 1);
      keyValue.append(String.format("%s=\"%s\";", k, v));
    }

    return keyValue.toString();
  }

}
