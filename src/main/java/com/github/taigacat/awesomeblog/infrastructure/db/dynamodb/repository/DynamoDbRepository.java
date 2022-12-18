package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbConfiguration;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbTableType;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.DynamoDbEntity;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@Requires(beans = {DynamoDbConfiguration.class, DynamoDbClient.class})
@Singleton
@Primary
public class DynamoDbRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbRepository.class);

  protected final DynamoDbClient dynamoDbClient;

  protected final DynamoDbEnhancedClient enhancedClient;
  protected final DynamoDbConfiguration dynamoConfiguration;
  private static final ObjectMapper mapper = new ObjectMapper();

  public DynamoDbRepository(DynamoDbConfiguration dynamoConfiguration,
      DynamoDbClient dynamoDbClient) {
    this.dynamoDbClient = dynamoDbClient;
    this.dynamoConfiguration = dynamoConfiguration;
    this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
  }

  public void createTable(DynamoDbTableType tableType) {
    final String HASH_KEY = "hashKey";
    final String RANGE_KEY = "rangeKey";

    boolean create = false;
    try {
      dynamoDbClient.describeTable(
          DescribeTableRequest.builder().tableName(tableType.getTableName(dynamoConfiguration))
              .build());
    } catch (ResourceNotFoundException e) {
      create = true;
    }

    if (create) {
      dynamoDbClient.createTable(CreateTableRequest.builder().attributeDefinitions(
              AttributeDefinition.builder().attributeName(HASH_KEY).attributeType(ScalarAttributeType.S)
                  .build(), AttributeDefinition.builder().attributeName(RANGE_KEY)
                  .attributeType(ScalarAttributeType.S).build()).keySchema(Arrays.asList(
              KeySchemaElement.builder().attributeName(HASH_KEY).keyType(KeyType.HASH).build(),
              KeySchemaElement.builder().attributeName(RANGE_KEY).keyType(KeyType.RANGE).build()))
          .billingMode(BillingMode.PAY_PER_REQUEST)
          .tableName(tableType.getTableName(dynamoConfiguration)).build());
    }
  }

  public <T extends DynamoDbEntity> PagingEntity<T> findAll(T tableSchema,
      @Nullable Integer limit,
      @Nullable String nextPageToken) {
    DynamoDbTable<T> table = this.getTable(tableSchema, enhancedClient, dynamoConfiguration);

    QueryEnhancedRequest.Builder request = QueryEnhancedRequest.builder().queryConditional(
        QueryConditional.keyEqualTo(
            Key.builder().partitionValue(tableSchema.getHashKey()).build()));
    if (limit != null) {
      request.limit(limit);
    }

    if (nextPageToken != null) {
      Map<String, AttributeValue> exclusiveStartKey = parseNextPageToken(nextPageToken);
      if (exclusiveStartKey != null) {
        request.exclusiveStartKey(exclusiveStartKey);
      }
    }

    QueryEnhancedRequest enhancedRequest = request.build();

    List<T> items = new ArrayList<>();
    Iterator<Page<T>> iterator = table.query(enhancedRequest).iterator();
    Map<String, AttributeValue> lastEvaluatedKey = null;
    while (iterator.hasNext()) {
      Page<T> page = iterator.next();
      lastEvaluatedKey = page.lastEvaluatedKey();
      items.addAll(page.items());
    }

    String newNextPageToken = nextPageToken(lastEvaluatedKey).orElse(null);

    PagingEntity<T> result = new PagingEntity<>(items, newNextPageToken);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("findAll " + result);
    }

    return result;
  }


  public <T extends DynamoDbEntity> Optional<T> findItem(T condition) {
    DynamoDbTable<T> table = this.getTable(condition, enhancedClient, dynamoConfiguration);

    try {
      T result = table.getItem(
          Key.builder().partitionValue(condition.getHashKey()).sortValue(condition.getRangeKey())
              .build());
      return Optional.ofNullable(result);
    } catch (DynamoDbException e) {
      LOGGER.error("Failed to get item", e);
      return Optional.empty();
    }
  }

  public <T extends DynamoDbEntity> void putItem(T entity) {
    DynamoDbTable<T> table = this.getTable(entity, enhancedClient, dynamoConfiguration);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(String.format("putItem [ item = %s]", entity));
    }

    try {
      table.putItem(entity);
    } catch (DynamoDbException e) {
      LOGGER.error("Failed to put item", e);
    }
  }

  public <T extends DynamoDbEntity> Optional<T> updateItem(T entity) {
    DynamoDbTable<T> table = this.getTable(entity, enhancedClient, dynamoConfiguration);

    try {
      T result = table.updateItem(entity);
      return Optional.ofNullable(result);
    } catch (DynamoDbException e) {
      LOGGER.error("Failed to update item", e);
      return Optional.empty();
    }
  }

  public <T extends DynamoDbEntity> Optional<T> deleteItem(T entity) {
    DynamoDbTable<T> table = this.getTable(entity, enhancedClient, dynamoConfiguration);

    try {
      T result = table.deleteItem(entity);
      return Optional.ofNullable(result);
    } catch (DynamoDbException e) {
      LOGGER.error("Failed to delete item", e);
      return Optional.empty();
    }
  }

  private <T extends DynamoDbEntity> DynamoDbTable<T> getTable(T tableSchema,
      DynamoDbEnhancedClient enhancedClient,
      DynamoDbConfiguration dynamoConfiguration) {
    @SuppressWarnings("unchecked ")
    Class<T> clazz = (Class<T>) tableSchema.getClass();
    TableSchema<T> schema = TableSchema.fromBean(clazz);
    return enhancedClient.table(
        tableSchema.getTableType().getTableName(dynamoConfiguration),
        schema
    );
  }

  @NonNull
  private static Optional<String> nextPageToken(
      @Nullable Map<String, AttributeValue> lastEvaluatedKey) {
    if (lastEvaluatedKey != null) {
      try {
        String str = mapper.writeValueAsString(lastEvaluatedKey);
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("lastEvaluatedKey = " + str);
        }
        return Optional.of(str);
      } catch (IOException e) {
        LOGGER.error("failed to serialize lastEvaluatedKey.", e);
      }
    }
    return Optional.empty();
  }

  @Nullable
  private static Map<String, AttributeValue> parseNextPageToken(@Nullable String nextPageToken) {
    if (nextPageToken != null && !nextPageToken.isEmpty()) {
      try {
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("nextPageToken = " + nextPageToken);
        }
        return mapper.readValue(nextPageToken,
            new TypeReference<LinkedHashMap<String, AttributeValue>>() {
            });
      } catch (JsonProcessingException e) {
        LOGGER.error("failed to deserialize nextPageToken.", e);
      }
    }
    return null;
  }
}
