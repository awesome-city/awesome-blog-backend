package com.github.awesome_city.blog.api.infrastructure.db.dynamodb.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.awesome_city.blog.api.constant.error.ResourceNotFoundException;
import com.github.awesome_city.blog.api.domain.common.PagingEntity;
import com.github.awesome_city.blog.api.domain.entity.Site;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@MicronautTest
class DynamoDbSiteRepositoryTest {

  @Inject
  DynamoDbSiteRepository repository;

  @Test
  void test_findById() {
    var id = "test_findById";
    var created = repository.create(
        Site.builder()
            .domain("test_findById_domain1")
            .title("title")
            .owner("taigacat")
            .build()
    );

    var optionalResult = repository.findById(created.getId());
    assertTrue(optionalResult.isPresent());
    var result = optionalResult.get();
    assertEquals(created.getId(), result.getId());
    assertEquals("test_findById_domain1", result.getDomain());
    assertEquals("title", result.getTitle());
  }

  @Test
  void test_findByDomain() {
    var id = "test_findByDomain";
    var site = repository.create(
        Site.builder()
            .domain("test_findByDomain_domain1")
            .title("title")
            .owner("taigacat")
            .build()
    );

    var optionalResult = repository.findByDomain("test_findByDomain_domain1");
    assertTrue(optionalResult.isPresent());
    var result = optionalResult.get();
    assertEquals(site.getId(), result.getId());
    assertEquals("test_findByDomain_domain1", result.getDomain());
    assertEquals("title", result.getTitle());
  }

  @Test
  void test_update() {
    var id = "test_update";
    var site = repository.create(
        Site.builder()
            .id(id)
            .domain("test_update_domain1")
            .title("title")
            .owner("taigacat")
            .build());
    site.setTitle("title_updated");
    var updated = repository.update(site);
    assertEquals(site.getId(), updated.getId());
    assertEquals(site.getDomain(), updated.getDomain());
    assertEquals("title_updated", updated.getTitle());

    var noExists = new Site();
    noExists.setId("test_update_noExists");
    noExists.setDomain("test_update_noExists");
    noExists.setTitle("noExists");
    noExists.setOwner("taigacat");
    assertThrows(ResourceNotFoundException.class, () -> repository.update(noExists));
  }

  @Test
  void test_delete() {
    var id = "test_delete";
    var site = repository.create(
        Site.builder()
            .id(id)
            .domain("test_delete_domain1")
            .title("title")
            .owner("taigacat")
            .build()
    );
    repository.delete(site.getId());
    PagingEntity<Site> result0 = repository.findAll(1000, null);
    assertNotNull(result0.getList());
    assertEquals(0,
        result0.getList().stream().filter(site1 -> id.equals(site1.getId())).toList().size());
  }
}
