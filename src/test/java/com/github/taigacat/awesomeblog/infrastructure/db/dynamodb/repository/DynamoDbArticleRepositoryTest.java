package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.domain.entity.Article.Status;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MicronautTest
class DynamoDbArticleRepositoryTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbArticleRepositoryTest.class);

  @Inject
  DynamoDbArticleRepository repository;

  private String tenantId;
  private int tenantNum;

  @BeforeEach
  void setUp() {
    tenantNum++;
    tenantId = String.format("tenant_%d", tenantNum);
  }

  @Test
  void test_findAll() {
    LOGGER.info("articles is empty");
    PagingEntity<Article> result0 = repository.findAll(tenantId, Status.PUBLISHED, 1000);
    assertNotNull(result0.getList());
    assertEquals(0, result0.getList().size());

    LOGGER.info("articles is 1");
    repository.create(
        new Article.Builder().status(Status.PUBLISHED).tenant(tenantId).name("hoge").build()
    );
    PagingEntity<Article> result = repository.findAll(tenantId, Status.PUBLISHED, 1000);
    assertNotNull(result.getList());
    assertEquals(1, result.getList().size());
    result.getList().forEach(article -> repository.delete(tenantId, article.getId()));
  }

  @Test
  void test_findById() {

  }
}
