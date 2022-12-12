package com.github.taigacat.awesomeblog.domain.repository;

import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;

public interface ArticleRepository {

  PagingEntity<Article> findAll(Integer limit);

  PagingEntity<Article> findAll(Integer limit, String nextPageToken);

  void put(Article article);

  void delete(String id);
}

