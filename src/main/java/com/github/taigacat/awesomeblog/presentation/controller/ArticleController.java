package com.github.taigacat.awesomeblog.presentation.controller;

import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.domain.repository.ArticleRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Status;
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

  @Get
  public PagingEntity<Article> getArticles() {
    LOGGER.info("in");
    PagingEntity<Article> result = repository.findAll(Article.Status.PUBLISHED, 100);
    LOGGER.info("out");
    return result;
  }

  @Delete("/{id}")
  @Status(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable @NonNull @NotBlank String id) {
    repository.delete(id);
  }
}
