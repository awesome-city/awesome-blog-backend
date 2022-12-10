package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.schema;

import com.github.taigacat.awesomeblog.domain.common.Identified;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.KeyAttribute;
import io.micronaut.core.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface DynamoDbEntity extends Identified {
	@NonNull
	String getHashKey();

	@NonNull
	String getRangeKey();

	@NonNull
	String getObjectName();

	default Integer getTtl() {
		return null;
	}

	default String createHashKeyValue(@NonNull List<KeyAttribute> keyAttributes) {
		List<KeyAttribute> keyAttributesWithObjectName = new ArrayList<>();
		keyAttributesWithObjectName.add(new KeyAttribute("object", this.getObjectName()));
		keyAttributesWithObjectName.addAll(keyAttributes);
		return this.createKeyValue(keyAttributesWithObjectName);
	}

	default String createRangeKeyValue(@NonNull List<KeyAttribute> keyAttributes) {
		if (keyAttributes.isEmpty()) {
			return " ";
		} else {
			return this.createKeyValue(keyAttributes);
		}
	}

	private String createKeyValue(List<KeyAttribute> keyAttributes) {
		return keyAttributes.stream()
				.map(KeyAttribute::join)
				.collect(Collectors.joining(""));
	}
}
