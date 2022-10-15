package com.github.taigacat.awesomeblog.db.entity;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
@Getter
@Setter
public class ArticleEntity extends BaseEntity {

	private String id;
	private String title;

	@Override
	protected String tableName() {
		return "article";
	}

	@Override
	protected String hashKey() {
		return this.createHashKey("id", this.id);
	}

	@Override
	protected String rangeKey() {
		return null;
	}
}
