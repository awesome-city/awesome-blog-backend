package com.github.awesome_city.blog.api.infrastructure.db.dynamodb.common;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import javax.validation.constraints.NotBlank;

@ConfigurationProperties("dynamodb")
public interface DynamoDbConfiguration {

  @NotBlank
  @Requires(property = "dynamodb.object-table-name")
  String getObjectTableName();

  @NotBlank
  @Requires(property = "dynamodb.relation-table-name")
  String getRelationTableName();
}
