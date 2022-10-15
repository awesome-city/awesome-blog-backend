package com.github.taigacat.awesomeblog.db.manager;

import lombok.Getter;

public enum TableType {
	object("awesome_blog_object_table"),
	relation("awesome_blog_relation_table");

	@Getter
	private final String tableName;

	TableType(String tableName) {
		this.tableName = tableName;
	}
}
