package com.github.taigacat.awesomeblog.domain.repository;

import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;
import io.micronaut.core.annotation.NonNull;
import java.util.Optional;

public interface ArticleRepository {

  PagingEntity<Article> findAll(
      @NonNull String tenant,
      @NonNull Article.Status status,
      Integer limit,
      String nextPageToken
  );

  Optional<Article> findById(
      @NonNull String tenant,
      @NonNull String id
  );

  Optional<Article> findByName(
      @NonNull String tenant,
      @NonNull String name
  );

  PagingEntity<Article> findByTag(
      @NonNull String tenant,
      @NonNull Article.Status status,
      @NonNull String tagId,
      Integer limit,
      String nextPageToken
  );

  PagingEntity<Article> findByAuthor(
      @NonNull String tenant,
      @NonNull Article.Status status,
      @NonNull String authorId,
      Integer limit,
      String nextPageToken
  );

  Article save(@NonNull Article article);

  void delete(
      @NonNull String tenant,
      @NonNull String id
  );
}
