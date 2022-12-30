package com.github.awesome_city.blog.api.infrastructure.db.dynamodb.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

@MicronautTest
class DynamoDbSupportTest {

  @Test
  void test_createHashKeyValue() {
    String key = DynamoDbSupport.createHashKeyValue("objectName", "a", "b");
    assertEquals("object=\"objectName\";a=\"b\";", key);
  }

  @Test
  void test_createRangeKeyValue() {
    String key = DynamoDbSupport.createRangeKeyValue("a", "b");
    assertEquals("a=\"b\";", key);
  }
}
