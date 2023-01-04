package com.github.awesome_city.blog.api.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.github.awesome_city.blog.api.domain.common.PagingEntity;
import com.github.awesome_city.blog.api.domain.entity.Site;
import com.github.awesome_city.blog.api.domain.repository.SiteRepository;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.function.aws.proxy.MicronautLambdaHandler;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@MicronautTest
class SiteControllerTest {

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
  void test_getSites() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("GET")
        .header("X-USER-ID", "taigacat")
        .path("/sites")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(200, response.getStatusCode());
  }

  @Test
  void test_createSite() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("POST")
        .path("/sites")
        .header("X-USER-ID", "taigacat")
        .body("{\"domain\":\"arakawa\",\"title\":\"荒川さんぽ\"}")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(201, response.getStatusCode());
  }

  @Test
  void test_getSiteById() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("GET")
        .header("X-USER-ID", "taigacat")
        .path("/sites/id1")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(200, response.getStatusCode());
  }

  @Test
  void test_updateSite() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("PUT")
        .header("X-USER-ID", "taigacat")
        .path("/sites/id1")
        .body("{\"domain\":\"arakawa2\",\"title\":\"荒川散歩２\"}")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(200, response.getStatusCode());
  }

  @Test
  void test_delete() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("DELETE")
        .path("/sites/id1")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(204, response.getStatusCode());
  }

  @Test
  void test_findByDomain() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("GET")
        .path("/sites/find-by-domain/arakawa")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(200, response.getStatusCode());
  }

  @Replaces(SiteRepository.class)
  @Singleton
  public static class SiteRepositoryStub implements SiteRepository {

    @Override
    public PagingEntity<Site> findAll(Integer limit, String nextPageToken) {
      return new PagingEntity<>(List.of(
          Site.builder().id("id1").title("title1").domain("domain1").owner("taigacat").build(),
          Site.builder().id("id2").title("title2").domain("domain2").owner("taigacat").build(),
          Site.builder().id("id3").title("title13").domain("domain3").owner("taigacat").build()
      ), null);
    }

    @Override
    public PagingEntity<Site> findByUser(String userId, Integer limit, String nextPageToken) {
      return new PagingEntity<>(List.of(
          Site.builder().id("id1").title("title1").domain("domain1").owner("taigacat").build(),
          Site.builder().id("id2").title("title2").domain("domain2").owner("taigacat").build(),
          Site.builder().id("id3").title("title13").domain("domain3").owner("taigacat").build()
      ), null);
    }

    @Override
    public Optional<Site> findById(String id) {
      return Optional.of(
          Site.builder().id("id1").title("title1").domain("domain1").owner("taigacat").build()
      );
    }

    @Override
    public Optional<Site> findByDomain(String domain) {
      return Optional.of(
          Site.builder().id("id1").title("title1").domain("domain1").owner("taigacat").build()
      );
    }

    @Override
    public Site create(@Valid Site site) {
      site.setId("id1");
      return site;
    }

    @Override
    public Site update(@Valid Site site) {
      return site;
    }

    @Override
    public void delete(String id) {

    }
  }
}
