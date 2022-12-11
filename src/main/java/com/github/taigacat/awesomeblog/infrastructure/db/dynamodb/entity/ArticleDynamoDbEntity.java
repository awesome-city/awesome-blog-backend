package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity;

import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbConfiguration;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.KeyAttribute;
import io.micronaut.core.annotation.NonNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.Collections;
import java.util.List;

@DynamoDbBean
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleDynamoDbEntity extends Article implements DynamoDbEntity<ArticleDynamoDbEntity> {

	private String hashKey;
	private String rangeKey;

	@NonNull
	public static ArticleDynamoDbEntity of(String id) {
		ArticleDynamoDbEntity entity = new ArticleDynamoDbEntity();
		entity.setId(id);
		return entity;
	}

	@Override
	public String getObjectName() {
		return "Article";
	}

	@Override
	@DynamoDbPartitionKey
	public String getHashKey() {
		return this.createHashKeyValue(Collections.emptyList());
	}

	@Override
	@DynamoDbSortKey
	public String getRangeKey() {
		return this.createRangeKeyValue(List.of(new KeyAttribute("id", getId())));
	}

	@Override
	public DynamoDbTable<ArticleDynamoDbEntity> getTable(
			DynamoDbEnhancedClient enhancedClient,
			DynamoDbConfiguration dynamoDbConfiguration
	) {
		TableSchema<ArticleDynamoDbEntity> tableSchema = TableSchema.builder(ArticleDynamoDbEntity.class)
				.newItemSupplier(ArticleDynamoDbEntity::new)
				.addAttribute(String.class, attr -> attr.name("hashKey")
						.getter(ArticleDynamoDbEntity::getHashKey)
						.setter(ArticleDynamoDbEntity::setHashKey)
						.tags(StaticAttributeTags.primaryPartitionKey()))
				.addAttribute(String.class, attr -> attr.name("rangeKey")
						.getter(ArticleDynamoDbEntity::getRangeKey)
						.setter(ArticleDynamoDbEntity::setHashKey)
						.tags(StaticAttributeTags.primarySortKey()))
				.build();

		return enhancedClient.table(
				dynamoDbConfiguration.getObjectTableName(),
				tableSchema
		);
	}
}
