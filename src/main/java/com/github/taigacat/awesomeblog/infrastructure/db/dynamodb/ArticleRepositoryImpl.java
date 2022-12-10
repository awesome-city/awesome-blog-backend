package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb;

import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.domain.repository.ArticleRepository;

public class ArticleRepositoryImpl implements ArticleRepository {
	@Override
	public PagingEntity<Article> findAll(int limit) {
		return null;
	}

	@Override
	public PagingEntity<Article> findAll(String nextPageToken, int limit) {
		return null;
	}
}
