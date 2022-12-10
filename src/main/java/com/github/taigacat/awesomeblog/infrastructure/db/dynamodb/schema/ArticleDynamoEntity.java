package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.schema;

import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.KeyAttribute;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.Collections;
import java.util.List;

@Data
public class ArticleDynamoEntity extends Article implements DynamoDbEntity {
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
	public String getObjectName() {
		return "Article";
	}
}
