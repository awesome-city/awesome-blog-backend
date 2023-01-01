package com.github.awesome_city.blog.api.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.github.awesome_city.blog.api.domain.common.PagingEntity;
import com.github.awesome_city.blog.api.domain.entity.Tag;
import com.github.awesome_city.blog.api.domain.repository.TagRepository;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.function.aws.proxy.MicronautLambdaHandler;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@MicronautTest
class TagControllerTest {

  private static MicronautLambdaHandler handler;
  private static final Context lambdaContext = new MockLambdaContext();

  @BeforeAll
  public static void setupSpec() {
    try {
      handler = new MicronautLambdaHandler();
    } catch (ContainerInitializationException e) {
      e.printStackTrace();
    }
  }

  @AfterAll
  public static void cleanupSpec() {
    handler.getApplicationContext().close();
  }

  @Test
  void test_getTags() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("GET")
        .path("/tags")
        .header("X-SITE-ID", "site1")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(200, response.getStatusCode());
  }

  @Test
  void test_createTag() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("POST")
        .path("/tags")
        .header("X-SITE-ID", "site1")
        .body("{\"name\":\"tagName1\",\"color\":\"#ddd\"}")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(201, response.getStatusCode());
  }

  @Test
  void test_getTagById() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("GET")
        .path("/tags/id1")
        .header("X-SITE-ID", "site1")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(200, response.getStatusCode());
  }

  @Test
  void test_updateTag() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("PUT")
        .path("/tags/id1")
        .header("X-SITE-ID", "site1")
        .body("{\"id\":\"id1\",\"name\":\"tagName1\",\"color\":\"#ddd\"}")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(200, response.getStatusCode());
  }

  @Test
  void test_delete() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("DELETE")
        .path("/tags/id1")
        .header("X-SITE-ID", "site1")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(204, response.getStatusCode());
  }

  @Replaces(TagRepository.class)
  @Singleton
  public static class TagRepositoryStub implements TagRepository {

    @Override
    public PagingEntity<Tag> findAll(String site, Integer limit, String nextPageToken) {
      return new PagingEntity<>(List.of(
          Tag.builder().id("id1").name("name1").site(site).color("#ddd").build(),
          Tag.builder().id("id2").name("name2").site(site).color("#ddd").build(),
          Tag.builder().id("id3").name("name3").site(site).color("#ddd").build()
      ), null);
    }

    @Override
    public Optional<Tag> findById(String site, String id) {
      return Optional.of(
          Tag.builder().id("id1").name("name1").site(site).color("#ddd").build()
      );
    }

    @Override
    public Tag create(Tag tag) {
      tag.setId("id1");
      return tag;
    }

    @Override
    public Tag update(Tag tag) {
      return tag;
    }

    @Override
    public void delete(String site, String id) {

    }
  }
}
