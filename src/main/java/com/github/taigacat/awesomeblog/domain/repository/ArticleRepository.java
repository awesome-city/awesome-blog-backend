package com.github.taigacat.awesomeblog.domain.repository;

import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;

public interface ArticleRepository {
	PagingEntity<Article> findAll(int limit);

	PagingEntity<Article> findAll(String nextPageToken, int limit);
}
