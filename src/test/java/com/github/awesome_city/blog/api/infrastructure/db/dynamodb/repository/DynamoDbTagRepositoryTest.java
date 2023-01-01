package com.github.awesome_city.blog.api.infrastructure.db.dynamodb.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.awesome_city.blog.api.constant.error.ResourceNotFoundException;
import com.github.awesome_city.blog.api.domain.common.PagingEntity;
import com.github.awesome_city.blog.api.domain.entity.Tag;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MicronautTest
class DynamoDbTagRepositoryTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbTagRepositoryTest.class);

  @Inject
  DynamoDbTagRepository repository;

  @Test
  void test_findAll() {
    var siteId = "test_findAll";

    LOGGER.info("tag is empty");
    PagingEntity<Tag> result0 = repository.findAll(siteId, 1000, null);
    assertNotNull(result0.getList());
    assertEquals(0, result0.getList().size());

    LOGGER.info("tag is 1");
    repository.create(Tag.builder().site(siteId).name("hoge").color("#ffffff").build());
    PagingEntity<Tag> result1 = repository.findAll(siteId, 1000, null);
    assertNotNull(result1.getList());
    assertEquals(1, result1.getList().size());
    result1.getList().forEach(tag -> repository.delete(siteId, tag.getId()));
  }

  @Test
  void test_findById() {
    var siteId = "test_findById";
    var tag = repository.create(Tag.builder().site(siteId).name("hoge").color("#ffffff").build());

    var result = repository.findById(siteId, tag.getId());
    assertTrue(result.isPresent());
    var tagResult = result.get();
    assertEquals(siteId, tagResult.getSite());
    assertEquals(tag.getId(), tagResult.getId());
    assertEquals("#ffffff", tagResult.getColor());
    assertEquals("hoge", tagResult.getName());
  }

  @Test
  void test_update() {
    var siteId = "test_update";
    var tag = repository.create(Tag.builder().site(siteId).name("hoge").color("#ffffff").build());
    tag.setName("fuga");
    tag.setColor("#000");
    var updated = repository.update(tag);
    assertEquals(tag.getId(), updated.getId());
    assertEquals(tag.getSite(), updated.getSite());
    assertEquals("fuga", updated.getName());
    assertEquals("#000", updated.getColor());

    var noExists = new Tag();
    noExists.setSite(siteId);
    noExists.setId("noExists");
    noExists.setName("noExists");
    noExists.setColor("#fff");
    assertThrows(ResourceNotFoundException.class, () -> repository.update(noExists));
  }

  @Test
  void test_delete() {
    var siteId = "test_delete";
    var tag = repository.create(Tag.builder().site(siteId).name("hoge").color("#ffffff").build());
    repository.delete(tag.getSite(), tag.getId());
    PagingEntity<Tag> result0 = repository.findAll(siteId, 1000, null);
    assertNotNull(result0.getList());
    assertEquals(0, result0.getList().size());
  }
}
