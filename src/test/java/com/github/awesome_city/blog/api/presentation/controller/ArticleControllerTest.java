package com.github.awesome_city.blog.api.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.github.awesome_city.blog.api.domain.common.PagingEntity;
import com.github.awesome_city.blog.api.domain.entity.Article;
import com.github.awesome_city.blog.api.domain.entity.Article.Status;
import com.github.awesome_city.blog.api.domain.repository.ArticleRepository;
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
class ArticleControllerTest {

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
  void test_getArticles_all() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("GET")
        .path("/articles")
        .header("X-SITE-ID", "site1")
        .queryString("status", "published")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(200, response.getStatusCode());
  }

  @Test
  void test_getArticles_name() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("GET")
        .path("/articles")
        .header("X-SITE-ID", "site1")
        .queryString("status", "published")
        .queryString("name", "name1")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(200, response.getStatusCode());
  }

  @Test
  void test_getArticles_tag() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("GET")
        .path("/articles")
        .header("X-SITE-ID", "site1")
        .queryString("status", "published")
        .queryString("tag", "tagA")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(200, response.getStatusCode());
  }

  @Test
  void test_getArticles_author() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("GET")
        .path("/articles")
        .header("X-SITE-ID", "site1")
        .queryString("status", "published")
        .queryString("author", "taigacat")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(200, response.getStatusCode());
  }

  @Test
  void getArticleById() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("GET")
        .path("/articles/id1")
        .header("X-SITE-ID", "site1")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(200, response.getStatusCode());
  }

  @Test
  void delete() {
    AwsProxyRequest request = new AwsProxyRequestBuilder()
        .method("DELETE")
        .path("/articles/id1")
        .header("X-SITE-ID", "site1")
        .build();
    AwsProxyResponse response = handler.handleRequest(request, lambdaContext);
    assertEquals(204, response.getStatusCode());
  }

  @Replaces(ArticleRepository.class)
  @Singleton
  public static class ArticleRepositoryStub implements ArticleRepository {

    @Override
    public PagingEntity<Article> findAll(String site, Status status, Integer limit,
        String nextPageToken) {
      return new PagingEntity<>(List.of(
          new Article.Builder().id("id1").name("name1").status(Status.PUBLISHED).build(),
          new Article.Builder().id("id2").name("name2").status(Status.PUBLISHED).build(),
          new Article.Builder().id("id3").name("name3").status(Status.PUBLISHED).build()
      ), null);
    }

    @Override
    public Optional<Article> findById(String site, String id) {
      return Optional.of(
          new Article.Builder().id("id1").name("name1").status(Status.PUBLISHED).build()
      );
    }

    @Override
    public Optional<Article> findByName(String site, String name) {
      return Optional.of(
          new Article.Builder().id("id1").name("name1").status(Status.PUBLISHED).build()
      );
    }

    @Override
    public PagingEntity<Article> findByTag(String site, Status status, String tagId,
        Integer limit, String nextPageToken) {
      return new PagingEntity<>(List.of(
          new Article.Builder().id("id1").name("name1").status(Status.PUBLISHED).build(),
          new Article.Builder().id("id2").name("name2").status(Status.PUBLISHED).build(),
          new Article.Builder().id("id3").name("name3").status(Status.PUBLISHED).build()
      ), null);
    }

    @Override
    public PagingEntity<Article> findByAuthor(String site, Status status, String authorId,
        Integer limit, String nextPageToken) {
      return new PagingEntity<>(List.of(
          new Article.Builder().id("id1").name("name1").status(Status.PUBLISHED).build(),
          new Article.Builder().id("id2").name("name2").status(Status.PUBLISHED).build(),
          new Article.Builder().id("id3").name("name3").status(Status.PUBLISHED).build()
      ), null);
    }

    @Override
    public Article create(Article article) {
      return article;
    }

    @Override
    public Article update(Article article) {
      return article;
    }

    @Override
    public void delete(String site, String id) {
    }
  }
}
