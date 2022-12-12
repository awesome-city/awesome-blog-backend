package com.github.taigacat.awesomeblog.domain.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@MicronautTest
class PagingEntityTest {

  @Test
  void list() {
    PagingEntity<String> pagingEntity = new PagingEntity<String>(List.of("test"), "token");
    assertEquals("token", pagingEntity.getNextPageToken());
  }

  @Test
  void nextPageToken() {
    PagingEntity<String> pagingEntity = new PagingEntity<String>(List.of("test"), "token");
    assertEquals("test", pagingEntity.getList().get(0));
  }

  @Test
  void testEquals() {
    EqualsVerifier.forClass(PagingEntity.class).verify();
  }
}
