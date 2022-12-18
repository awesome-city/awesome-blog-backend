package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.repository;

import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.domain.entity.Article.Status;
import com.github.taigacat.awesomeblog.domain.repository.ArticleRepository;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbConfiguration;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.article.ArticleNameRelation;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.article.ArticleObject;
import com.github.taigacat.awesomeblog.util.uuid.IdGenerator;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Singleton
public class DynamoDbArticleRepository extends DynamoDbRepository implements
    ArticleRepository {

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
  public PagingEntity<Article> findAll(String tenant, Article.Status status, Integer limit) {
    return this.findAll(tenant, status, limit, null);
  }

  @Override
  public PagingEntity<Article> findAll(String tenant, Article.Status status, Integer limit,
      String nextPageToken) {
    LOGGER.info("in");
    PagingEntity<ArticleObject> dynamoEntity = findAll(
        ArticleObject.of(new Article.Builder().tenant(tenant).status(status).build()),
        limit,
        nextPageToken
    );

    LOGGER.info("out");
    return new PagingEntity<>(
        dynamoEntity.getList().stream()
            .map(e -> (Article) e)
            .collect(Collectors.toList()),
        dynamoEntity.getNextPageToken()
    );
  }

  @Override
  public Optional<Article> findById(@NonNull String tenant, @NonNull Article.Status status,
      @NonNull String id) {
    LOGGER.info("in");
    LOGGER.debug("find article by id [id = " + id + "]");
    Optional<ArticleObject> object = findItem(ArticleObject.of(
        new Article.Builder().tenant(tenant).status(status).id(id).build()
    ));
    if (object.isPresent()) {
      LOGGER.info("article found [id = " + object.get().getId() + "]");
    } else {
      LOGGER.info("article not found");
    }
    LOGGER.info("out");
    return object.map(e -> e);
  }

  @Override
  public Optional<Article> findByName(String tenant, String name) {
    LOGGER.info("in");
    LOGGER.debug("find article by name [name = " + name + "]");
    ArticleNameRelation articleNameRelation = new ArticleNameRelation(tenant, name);
    Optional<ArticleNameRelation> relation = findItem(articleNameRelation);
    if (relation.isPresent()) {
      LOGGER.info("relation found [id = " + relation.get().getId() + "]");
    } else {
      LOGGER.info("relation not found");
      return Optional.empty();
    }

    Optional<Article> object = this.findById(tenant, Status.PUBLISHED, relation.get().getId());
    LOGGER.info("out");
    return object;
  }

  @Override
  public void create(Article article) {
    LOGGER.info("in");

    // Object
    ArticleObject object = ArticleObject.of(article);
    object.setId(idGenerator.generate());
    LOGGER.info("put article entity [ id = " + object.getId() + "]");
    putItem(object);

    // Relation
    ArticleNameRelation nameRelation = new ArticleNameRelation(object);
    putItem(nameRelation);

    LOGGER.info("out");
  }

  @Override
  public void delete(String tenant, String id) {
    LOGGER.info("in");

    Consumer<ArticleObject> deleteRelations = (article) -> {
      ArticleNameRelation nameRelation = new ArticleNameRelation(article);
      deleteItem(nameRelation);
    };

    this.findById(tenant, Status.PUBLISHED, id)
        .ifPresentOrElse(
            article -> deleteItem(ArticleObject.of(
                new Article.Builder().tenant(tenant).status(Status.PUBLISHED).id(id).build()
            )).ifPresent(deleteRelations),
            () -> deleteItem(ArticleObject.of(
                new Article.Builder().tenant(tenant).status(Status.DRAFT).id(id).build()
            )).ifPresent(deleteRelations)
        );
    LOGGER.info("out");
  }
}
