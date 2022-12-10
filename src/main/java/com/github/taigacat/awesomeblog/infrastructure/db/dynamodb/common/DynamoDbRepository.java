package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.schema.DynamoDbEntity;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Requires(beans = {DynamoDbConfiguration.class, DynamoDbClient.class})
@Singleton
@Primary
public abstract class DynamoDbRepository<T extends DynamoDbEntity> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbRepository.class);

	protected static final String HASH_KEY = "hashKey";
	protected static final String RANGE_KEY = "rangeKey";
	protected static final String TTL_KEY = "ttl";

	protected final DynamoDbClient dynamoDbClient;

	protected final DynamoDbEnhancedClient enhancedClient;
	protected final DynamoDbConfiguration dynamoConfiguration;
	private static final ObjectMapper mapper = new ObjectMapper();

	public DynamoDbRepository(
			DynamoDbConfiguration dynamoConfiguration,
			DynamoDbClient dynamoDbClient
	) {
		this.dynamoDbClient = dynamoDbClient;
		this.dynamoConfiguration = dynamoConfiguration;
		this.enhancedClient = DynamoDbEnhancedClient.builder()
				.dynamoDbClient(dynamoDbClient)
				.build();
	}

	protected abstract String getObjectName();

	protected abstract List<KeyAttribute> getHashKeyAttribute();

	protected abstract List<KeyAttribute> getRangeKeyAttribute();

	protected Integer getTtl() {
		return null;
	}

	public PagingEntity<T> findAll(T condition, @Nullable Integer limit) {
//		DynamoDbTable<? extends DynamoDbEntity> table = enhancedClient.table(dynamoConfiguration.getObjectTableName(), TableSchema.fromBean(condition.getClass()));
//
//		QueryEnhancedRequest.Builder request = QueryEnhancedRequest.builder()
//				.queryConditional(QueryConditional.keyEqualTo(
//						Key.builder().partitionValue(condition.getHashKey()).build()
//				));
//
//		if (limit != null) {
//			request.limit(limit);
//		}
//
//		if (nextPageToken != null) {
//			request.exclusiveStartKey()
//		}
//
//		table.
//
//		PageIterable<? extends DynamoDbEntity> iterable = table.query(request.build());
		// TODO 実装
		return null;
	}

	@NonNull
	public static Optional<String> lastEvaluatedKey(@NonNull QueryResponse response) {
		if (response.hasLastEvaluatedKey()) {
			Map<String, AttributeValue> item = response.lastEvaluatedKey();
			if (item != null) {
				try {
					String str = mapper.writeValueAsString(item);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("lastEvaluatedKey = " + str);
					}
					return Optional.of(str);
				} catch (IOException e) {
					LOGGER.error("failed to deserialize lastEvaluatedKey.", e);
					throw new RuntimeException(e);
				}
			}
		}
		return Optional.empty();
	}

	public static Map<String, AttributeValue> parseLastEvaluatedKey(@NonNull String lastEvaluatedKey) {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("lastEvaluatedKey = " + lastEvaluatedKey);
			}
			return mapper.readValue(
					lastEvaluatedKey,
					new TypeReference<LinkedHashMap<String, AttributeValue>>() {
					}
			);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}


}
