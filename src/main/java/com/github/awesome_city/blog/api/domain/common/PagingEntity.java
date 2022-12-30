package com.github.awesome_city.blog.api.domain.common;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Data
public final class PagingEntity<T> {

	private List<T> list;
	private String nextPageToken;

	public PagingEntity() {
	}

	public PagingEntity(List<T> list, String nextPageToken) {
		this.list = list;
		this.nextPageToken = nextPageToken;
	}
}
