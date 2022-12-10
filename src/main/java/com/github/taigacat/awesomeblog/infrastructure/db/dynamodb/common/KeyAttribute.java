package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common;

import java.util.Objects;

public final class KeyAttribute {
	private final String name;
	private final String value;

	public KeyAttribute(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String join() {
		return name + "=" + "\"" + value + "\";";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (KeyAttribute) obj;
		return Objects.equals(this.name, that.name) &&
				Objects.equals(this.value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, value);
	}

	@Override
	public String toString() {
		return "KeyAttribute[" +
				"name=" + name + ", " +
				"value=" + value + ']';
	}

}
