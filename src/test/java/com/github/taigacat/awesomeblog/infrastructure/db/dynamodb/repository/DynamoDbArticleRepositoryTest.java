package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MicronautTest
class DynamoDbArticleRepositoryTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbArticleRepositoryTest.class);

  @Inject
  DynamoDbArticleRepository repository;

  @Test
  void findAll() {
    PagingEntity<Article> result0 = repository.findAll(1000);
    assertNotNull(result0.getList());
    assertEquals(0, result0.getList().size());

    repository.create(new Article());
    PagingEntity<Article> result = repository.findAll(1000);
    assertNotNull(result.getList());
    assertEquals(1, result.getList().size());
    result.getList().forEach(article -> repository.delete(article.getId()));
  }
}
