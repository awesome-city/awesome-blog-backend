package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.domain.entity.Article.Status;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MicronautTest
class DynamoDbArticleRepositoryTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbArticleRepositoryTest.class);

  @Inject
  DynamoDbArticleRepository repository;

  @Test
  void test_findAll() {
    var siteId = "test_findAll";

    LOGGER.info("articles is empty");
    PagingEntity<Article> result0 = repository.findAll(siteId, Status.PUBLISHED, 1000, null);
    assertNotNull(result0.getList());
    assertEquals(0, result0.getList().size());

    LOGGER.info("articles is 1");
    var article1 = repository.create(
        new Article.Builder().status(Status.PUBLISHED).site(siteId).name("hoge")
            .author("taigacat").build()
    );
    PagingEntity<Article> result = repository.findAll(siteId, Status.PUBLISHED, 1000, null);
    assertNotNull(result.getList());
    assertEquals(1, result.getList().size());
    result.getList().forEach(article -> repository.delete(siteId, article.getId()));
  }

  @Test
  void test_findById() {
    var siteId = "test_findById";
    var article = repository.create(
        new Article.Builder().status(Status.PUBLISHED).site(siteId).name("hoge")
            .author("taigacat").build()
    );

    var result = repository.findById(siteId, article.getId());
    assertTrue(result.isPresent());
    var articleResult = result.get();
    assertEquals(siteId, articleResult.getSite());
    assertEquals("hoge", articleResult.getName());
    assertEquals(Status.PUBLISHED, articleResult.getStatus());
  }

  @Test
  void test_findByName() {
    var siteId = "test_findByName";
    var article = repository.create(
        new Article.Builder().status(Status.PUBLISHED).site(siteId).name("hoge")
            .author("taigacat").build()
    );
    var result = repository.findByName(siteId, article.getName());
    assertTrue(result.isPresent());
    var articleResult = result.get();
    assertEquals(siteId, articleResult.getSite());
    assertEquals("hoge", articleResult.getName());
    assertEquals(Status.PUBLISHED, articleResult.getStatus());
  }

  @Test
  void test_findByTag() {
    var siteId = "test_findByTag";
    repository.create(
        new Article.Builder()
            .status(Status.PUBLISHED)
            .site(siteId)
            .name("hoge1")
            .author("taigacat")
            .tags(Set.of("tagA", "tagB"))
            .build()
    );
    repository.create(
        new Article.Builder()
            .status(Status.PUBLISHED)
            .site(siteId)
            .name("hoge2")
            .author("taigacat")
            .tags(Set.of("tagA", "tagB"))
            .build()
    );

    // ALL
    var result = repository.findByTag(siteId, Status.PUBLISHED, "tagA", 2, null);
    assertNotNull(result);
    assertNotNull(result.getList());
    assertEquals(2, result.getList().size());
    Article article_all = result.getList().get(0);
    assertEquals(siteId, article_all.getSite());

    // PARTIAL
    var partialResult = repository.findByTag(siteId, Status.PUBLISHED, "tagA", 1, null);
    assertNotNull(partialResult);
    assertNotNull(partialResult.getList());
    assertEquals(1, partialResult.getList().size());
    Article article_partial = partialResult.getList().get(0);
    assertEquals(siteId, article_partial.getSite());

    // NEXT
    var nextResult = repository.findByTag(siteId, Status.PUBLISHED, "tagA", 100,
        partialResult.getNextPageToken());
    assertNotNull(nextResult);
    assertNotNull(nextResult.getList());
    assertEquals(1, nextResult.getList().size());
    Article article_next = nextResult.getList().get(0);
    assertEquals(siteId, article_next.getSite());
  }

  @Test
  void test_findByAuthor() {
    var siteId = "test_findByAuthor";
    repository.create(
        new Article.Builder()
            .status(Status.PUBLISHED)
            .site(siteId)
            .name("hoge1")
            .author("taigacat")
            .tags(Set.of("tagA", "tagB"))
            .build()
    );
    repository.create(
        new Article.Builder()
            .status(Status.PUBLISHED)
            .site(siteId)
            .name("hoge2")
            .author("taigacat")
            .tags(Set.of("tagA", "tagB"))
            .build()
    );

    // ALL
    var result = repository.findByAuthor(siteId, Status.PUBLISHED, "taigacat", 2, null);
    assertNotNull(result);
    assertNotNull(result.getList());
    assertEquals(2, result.getList().size());
    Article article_all = result.getList().get(0);
    assertEquals(siteId, article_all.getSite());

    // PARTIAL
    var partialResult = repository.findByAuthor(siteId, Status.PUBLISHED, "taigacat", 1, null);
    assertNotNull(partialResult);
    assertNotNull(partialResult.getList());
    assertEquals(1, partialResult.getList().size());
    Article article_partial = partialResult.getList().get(0);
    assertEquals(siteId, article_partial.getSite());

    // NEXT
    var nextResult = repository.findByAuthor(siteId, Status.PUBLISHED, "taigacat", 100,
        partialResult.getNextPageToken());
    assertNotNull(nextResult);
    assertNotNull(nextResult.getList());
    assertEquals(1, nextResult.getList().size());
    Article article_next = nextResult.getList().get(0);
    assertEquals(siteId, article_next.getSite());
  }
}
