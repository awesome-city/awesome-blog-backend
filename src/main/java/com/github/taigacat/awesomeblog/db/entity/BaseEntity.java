package com.github.taigacat.awesomeblog.db.entity;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public abstract class BaseEntity {

	public BaseEntity() {
	}

	public String createHashKey(String... kv) {
		if (kv.length % 2 != 0) {
			throw new IllegalArgumentException("argument length must be even.");
		}
		return createKeyValuePair("tableName", this.tableName())
				+ createKayValuePairAll(kv);
	}

	public String createRangeKey(String... kv) {
		if (kv.length % 2 != 0) {
			throw new IllegalArgumentException("argument length must be even.");
		}
		return createKayValuePairAll(kv);
	}

	protected abstract String tableName();

	protected abstract String hashKey();

	protected abstract String rangeKey();

	@DynamoDbPartitionKey
	public String getHashKey() {
		return this.hashKey();
	}

	@DynamoDbSortKey
	public String getRangeKey() {
		return this.rangeKey();
	}

	public Integer getTtl() {
		return null;
	}

	private String createKayValuePairAll(String... kv) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i + 1 < kv.length; i = i + 2) {
			sb.append(createKeyValuePair(kv[i], kv[i + 1]));
		}
		return sb.toString();
	}

	private String createKeyValuePair(String key, String value) {
		return "\"" + key + "=" + value + "\";";
	}
}
