package com.github.taigacat.awesomeblog.presentation.controller;

import com.github.taigacat.awesomeblog.constant.error.ResourceNotFoundException;
import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.domain.repository.ArticleRepository;
import com.github.taigacat.awesomeblog.util.aspect.Log;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.annotation.Status;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/articles")
public class ArticleController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);
  private final ArticleRepository repository;

  public ArticleController(ArticleRepository articleRepository) {
    this.repository = articleRepository;
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
  @Log
  public PagingEntity<Article> getArticles(
      @Header("X-TENANT-ID") String tenant,
      @QueryValue String status,
      @QueryValue Optional<String> name,
      @QueryValue Optional<String> tag,
      @QueryValue Optional<String> author,
      @QueryValue Optional<Integer> limit,
      @QueryValue Optional<String> nextPageToken
  ) {
    Article.Status statusEnum = Article.Status.byName(status);

    PagingEntity<Article> result;
    if (name.isPresent()) {
      Optional<Article> findByNameResult = repository.findByName(tenant, name.get());
      result = findByNameResult
          .map(article -> new PagingEntity<>(List.of(article), null))
          .orElse(null);
    } else if (tag.isPresent()) {
      result = repository.findByTag(
          tenant,
          statusEnum,
          tag.get(),
          limit.orElse(100),
          nextPageToken.orElse(null)
      );
    } else if (author.isPresent()) {
      result = repository.findByAuthor(
          tenant,
          statusEnum,
          author.get(),
          limit.orElse(100),
          nextPageToken.orElse(null)
      );
    } else {
      result = repository.findAll(
          tenant,
          statusEnum,
          limit.orElse(100),
          nextPageToken.orElse(null)
      );
    }

    return result;
  }

  @Get("/{id}")
  @Log
  public Article getArticleById(
      @Header("X-TENANT-ID") String tenant,
      @PathVariable @NonNull @NotBlank String id
  ) {
    Optional<Article> optional = repository.findById(tenant, id);

    return optional.orElseThrow(
        () -> new ResourceNotFoundException("article not found"));
  }

  @Delete("/{id}")
  @Log
  @Status(HttpStatus.NO_CONTENT)
  public void delete(
      @Header("X-TENANT-ID") String tenant,
      @PathVariable @NonNull @NotBlank String id
  ) {
    repository.delete(tenant, id);
  }
}
