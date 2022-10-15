package com.github.taigacat.awesomeblog.db.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectEntityTest {

	static class ObjectEntityExample extends BaseEntity {

		@Override
		protected String tableName() {
			return "example_table";
		}

		@Override
		protected String hashKey() {
			return this.createHashKey("hoge", "fuga");
		}

		@Override
		protected String rangeKey() {
			return this.createRangeKey("hoge", "fuga", "fiz", "buz");
		}
	}

	@Test
	void createHashKey() {
		ObjectEntityExample objectEntityExample = new ObjectEntityExample();
		assertEquals("\"tableName=example_table\";\"hoge=fuga\";", objectEntityExample.getHashKey());
	}

	@Test
	void createRangeKey() {
		ObjectEntityExample objectEntityExample = new ObjectEntityExample();
		assertEquals("\"hoge=fuga\";\"fiz=buz\";", objectEntityExample.getRangeKey());
	}
}
