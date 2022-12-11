package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.repository;

import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.domain.repository.ArticleRepository;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbConfiguration;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.ArticleDynamoDbEntity;
import com.github.taigacat.awesomeblog.util.uuid.IdGenerator;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.stream.Collectors;

@Singleton
public class DynamoDbArticleRepository extends DynamoDbRepository<ArticleDynamoDbEntity> implements ArticleRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbArticleRepository.class);

	private final IdGenerator idGenerator;

	public DynamoDbArticleRepository(
			DynamoDbConfiguration dynamoConfiguration,
			DynamoDbClient dynamoDbClient,
			IdGenerator idGenerator
	) {
		super(dynamoConfiguration, dynamoDbClient);
		this.idGenerator = idGenerator;
	}

	@Override
	public PagingEntity<Article> findAll(Integer limit) {
		return this.findAll(limit, null);
	}

	@Override
	public PagingEntity<Article> findAll(Integer limit, String nextPageToken) {
		LOGGER.info("in");
		PagingEntity<ArticleDynamoDbEntity> dynamoEntity = findAll(
				new ArticleDynamoDbEntity(),
				limit,
				nextPageToken
		);
		LOGGER.info("out");
		return new PagingEntity<>(
				dynamoEntity.list().stream()
						.map(e -> (Article) e)
						.collect(Collectors.toList()),
				dynamoEntity.nextPageToken()
		);
	}

	@Override
	public void put(Article article) {
		LOGGER.info("in");
		ArticleDynamoDbEntity dynamoDbEntity = ArticleDynamoDbEntity.of(idGenerator.generate());
		LOGGER.debug("put article entity [" + dynamoDbEntity + "]");
		put(dynamoDbEntity);
		LOGGER.info("out");
	}

	@Override
	public void delete(String id) {
		LOGGER.info("in");
		delete(ArticleDynamoDbEntity.of(id));
		LOGGER.info("out");
	}
}
