package com.github.taigacat.awesomeblog.domain.common;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode
@ToString
public final class PagingEntity<T> {
	private final List<T> list;
	private final String nextPageToken;

	public PagingEntity(List<T> list, String nextPageToken) {
		this.list = list;
		this.nextPageToken = nextPageToken;
	}

	public List<T> list() {
		return list;
	}

	public String nextPageToken() {
		return nextPageToken;
	}
}
