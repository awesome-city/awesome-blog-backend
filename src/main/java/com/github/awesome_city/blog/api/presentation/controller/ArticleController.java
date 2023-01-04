package com.github.awesome_city.blog.api.presentation.controller;

import com.github.awesome_city.blog.api.constant.error.BadRequestException;
import com.github.awesome_city.blog.api.constant.error.ResourceNotFoundException;
import com.github.awesome_city.blog.api.domain.common.PagingEntity;
import com.github.awesome_city.blog.api.domain.entity.Article;
import com.github.awesome_city.blog.api.domain.repository.ArticleRepository;
import com.github.awesome_city.blog.api.util.aspect.Log;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.annotation.Status;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.validator.Validator;
import java.util.Optional;
import javax.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/articles")
public class ArticleController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);
  private final ArticleRepository repository;
  private final Validator validator;

  public ArticleController(
      ArticleRepository articleRepository,
      Validator validator
  ) {
    this.repository = articleRepository;
    this.validator = validator;
  }

  /**
   * 記事一覧を取得します
   *
   * @param status        記事ステータス
   * @param tag           タグ
   * @param author        著者
   * @param limit         1度に取得する件数
   * @param nextPageToken 次ページ取得トークン
   * @return 記事一覧
   */
  @Get
  @Secured(SecurityRule.IS_ANONYMOUS)
  @Log
  public PagingEntity<Article> getArticles(
      @Header("X-SITE-ID") String site,
      @QueryValue String status,
      @QueryValue Optional<String> tag,
      @QueryValue Optional<String> author,
      @QueryValue Optional<Integer> limit,
      @QueryValue Optional<String> nextPageToken
  ) {
    Article.Status statusEnum = Article.Status.byName(status);

    PagingEntity<Article> result;
    if (tag.isPresent()) {
      result = repository.findByTag(
          site,
          statusEnum,
          tag.get(),
          limit.orElse(100),
          nextPageToken.orElse(null)
      );
    } else if (author.isPresent()) {
      result = repository.findByAuthor(
          site,
          statusEnum,
          author.get(),
          limit.orElse(100),
          nextPageToken.orElse(null)
      );
    } else {
      result = repository.findAll(
          site,
          statusEnum,
          limit.orElse(100),
          nextPageToken.orElse(null)
      );
    }

    return result;
  }

  @Post
  @Status(HttpStatus.CREATED)
  @Log
  public Article createArticle(
      @Header("X-SITE-ID") String site,
      @Body Article article
  ) {
    article.setSite(site);

    if (article.validate(validator)) {
      return repository.create(article);
    } else {
      throw new BadRequestException("article has violations");
    }
  }

  @Get("/{id}")
  @Secured(SecurityRule.IS_ANONYMOUS)
  @Log
  public Article getArticleById(
      @Header("X-SITE-ID") String site,
      @PathVariable @NonNull @NotBlank String id
  ) {
    return repository.findById(site, id)
        .orElseThrow(() -> new ResourceNotFoundException("article not found"));
  }

  @Put("/{id}")
  @Log
  public Article updateArticle(
      @Header("X-SITE-ID") String site,
      @PathVariable @NonNull @NotBlank String id,
      @Body Article article
  ) {
    article.setSite(site);
    article.setId(id);

    if (article.validate(validator)) {
      return repository.update(article);
    } else {
      throw new BadRequestException("article has violations");
    }
  }

  @Delete("/{id}")
  @Log
  @Status(HttpStatus.NO_CONTENT)
  public void delete(
      @Header("X-SITE-ID") String site,
      @PathVariable @NonNull @NotBlank String id
  ) {
    repository.delete(site, id);
  }

  @Get("/find-by-name/{name}")
  @Secured(SecurityRule.IS_ANONYMOUS)
  @Log
  public Article findByName(
      @Header("X-SITE-ID") String site,
      @PathVariable @NotBlank String name
  ) {
    return repository.findByName(site, name)
        .orElseThrow(() -> new ResourceNotFoundException("article not found"));
  }
}
