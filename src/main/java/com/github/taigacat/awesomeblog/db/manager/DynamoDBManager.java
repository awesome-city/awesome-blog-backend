package com.github.taigacat.awesomeblog.db.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.taigacat.awesomeblog.db.entity.BaseEntity;
import com.github.taigacat.awesomeblog.utils.JsonUtils;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

public class DynamoDBManager {

	private final TableType type;
	private final DynamoDbEnhancedClient client;

	public DynamoDBManager(TableType type) {
		this.type = type;

		DynamoDbClient client = DynamoDbClient.builder()
				.region(Region.of(System.getenv("AWS_REGION")))
				.build();
		this.client = DynamoDbEnhancedClient.builder()
				.dynamoDbClient(client)
				.build();
	}

	public <T extends BaseEntity> T loadOne(T bean) {
		DynamoDbTable<T> table = this.getTable(bean);
		return table.getItem(
				Key.builder()
						.partitionValue(bean.getHashKey())
						.sortValue(bean.getRangeKey())
						.build()
		);
	}

	public <T extends BaseEntity> PageResult<T> loadManyByHashKey(T bean, int limit) {
		return this.loadManyByHashKey(bean, limit, null);
	}

	public <T extends BaseEntity> PageResult<T> loadManyByHashKey(T bean, int limit, String startKey) {
		DynamoDbTable<T> table = this.getTable(bean);
		PageIterable<T> result = table.query(QueryEnhancedRequest.builder()
				.queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(bean.getHashKey()).build()))
				.exclusiveStartKey(JsonUtils.readValue(startKey, new TypeReference<Map<String, AttributeValue>>() {
				}).orElse(null))
				.limit(limit)
				.build());
		List<T> list = result.items().stream().toList();
		Map<String, AttributeValue> lastEvaluatedKey = result.stream().findFirst().map(Page::lastEvaluatedKey).orElse(null);
		String lastEvaluatedKeyStr = JsonUtils.writeValueAsString(lastEvaluatedKey).orElse(null);
		return new PageResult<>(list, lastEvaluatedKeyStr);
	}

	private <T extends BaseEntity> DynamoDbTable<T> getTable(T bean) {
		@SuppressWarnings("unchecked")
		Class<T> clz = (Class<T>) bean.getClass();
		return this.client.table(this.type.getTableName(), TableSchema.fromBean(clz));
	}

}
