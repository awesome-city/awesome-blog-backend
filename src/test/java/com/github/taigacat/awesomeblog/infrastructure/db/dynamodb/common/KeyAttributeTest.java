package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class KeyAttributeTest {

	@Test
	void join() {
		KeyAttribute keyAttribute = new KeyAttribute("k", "v");
		assertEquals("k=\"v\";", keyAttribute.join());
	}

	@Test
	void testEquals() {
		EqualsVerifier.forClass(KeyAttribute.class).verify();
	}
}
