package com.github.taigacat.awesomeblog.domain.repository;

import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;
import java.util.Optional;

public interface ArticleRepository {

  PagingEntity<Article> findAll(Article.Status status, Integer limit);

  PagingEntity<Article> findAll(Article.Status status, Integer limit, String nextPageToken);

  Optional<Article> findById(Article.Status status, String id);

  Optional<Article> findByName(String name);

  void create(Article article);

  void delete(String id);
}

