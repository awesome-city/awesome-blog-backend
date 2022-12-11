package com.github.taigacat.awesomeblog.domain.common;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class PagingEntityTest {

	@Test
	void list() {
		PagingEntity<String> pagingEntity = new PagingEntity<String>(List.of("test"), "token");
		assertEquals("token", pagingEntity.nextPageToken());
	}

	@Test
	void nextPageToken() {
		PagingEntity<String> pagingEntity = new PagingEntity<String>(List.of("test"), "token");
		assertEquals("test", pagingEntity.list().get(0));
	}

	@Test
	void testEquals() {
		EqualsVerifier.forClass(PagingEntity.class).verify();
	}
}