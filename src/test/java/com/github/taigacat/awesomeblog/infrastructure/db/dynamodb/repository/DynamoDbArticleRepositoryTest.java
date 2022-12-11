package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.repository;

import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
class DynamoDbArticleRepositoryTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbArticleRepositoryTest.class);

	@Inject
	DynamoDbArticleRepository repository;

	@Test
	void findAll() {
		PagingEntity<Article> result0 = repository.findAll(1000);
		assertNotNull(result0.list());
		assertEquals(0, result0.list().size());

		repository.put(new Article());
		PagingEntity<Article> result = repository.findAll(1000);
		assertNotNull(result.list());
		assertEquals(1, result.list().size());
		result.list().forEach(article -> repository.delete(article.getId()));
	}
}