package com.github.taigacat.awesomeblog.presentation.controller;

import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.presentation.model.PagingResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.util.List;

@Controller("/articles")
public class ArticleController {

	@Get
	public PagingResponse<Article> getArticles() {
		return new PagingResponse<>(List.of(new Article()), "token");
	}
}
