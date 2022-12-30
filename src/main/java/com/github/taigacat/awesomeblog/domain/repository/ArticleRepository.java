package com.github.taigacat.awesomeblog.domain.repository;

import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;
import io.micronaut.core.annotation.NonNull;
import java.util.Optional;

public interface ArticleRepository {

  PagingEntity<Article> findAll(
      @NonNull String site,
      @NonNull Article.Status status,
      Integer limit,
      String nextPageToken
  );

  Optional<Article> findById(
      @NonNull String site,
      @NonNull String id
  );

  Optional<Article> findByName(
      @NonNull String site,
      @NonNull String name
  );

  PagingEntity<Article> findByTag(
      @NonNull String site,
      @NonNull Article.Status status,
      @NonNull String tagId,
      Integer limit,
      String nextPageToken
  );

  PagingEntity<Article> findByAuthor(
      @NonNull String site,
      @NonNull Article.Status status,
      @NonNull String authorId,
      Integer limit,
      String nextPageToken
  );

  Article create(@NonNull Article article);

  Article update(@NonNull Article article);

  void delete(
      @NonNull String site,
      @NonNull String id
  );
}
