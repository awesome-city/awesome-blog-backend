package com.github.taigacat.awesomeblog.domain.repository;

import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;
import java.util.Optional;

public interface ArticleRepository {

  PagingEntity<Article> findAll(Integer limit);

  PagingEntity<Article> findAll(Integer limit, String nextPageToken);

  Optional<Article> findById(String id);

  Optional<Article> findByName(String name);

  void create(Article article);

  void delete(String id);
}

