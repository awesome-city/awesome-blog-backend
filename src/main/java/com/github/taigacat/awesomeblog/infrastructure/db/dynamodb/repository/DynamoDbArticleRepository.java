package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.repository;

import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.domain.entity.Article.Status;
import com.github.taigacat.awesomeblog.domain.repository.ArticleRepository;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbConfiguration;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.article.ArticleAuthorRelation;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.article.ArticleNameRelation;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.article.ArticleObject;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.article.ArticleTagRelation;
import com.github.taigacat.awesomeblog.util.CollectionUtils;
import com.github.taigacat.awesomeblog.util.JsonMapper;
import com.github.taigacat.awesomeblog.util.aspect.Log;
import com.github.taigacat.awesomeblog.util.id.IdGenerator;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import java.util.List;
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
      JsonMapper jsonMapper,
      IdGenerator idGenerator
  ) {
    super(dynamoConfiguration, dynamoDbClient, jsonMapper);
    this.idGenerator = idGenerator;
  }

  @Override
  @Log
  public PagingEntity<Article> findAll(String tenant, Article.Status status, Integer limit,
      String nextPageToken) {
    PagingEntity<ArticleObject> dynamoEntity = findAllItems(
        ArticleObject.of(new Article.Builder().tenant(tenant).status(status).build()),
        limit,
        nextPageToken
    );

    return new PagingEntity<>(
        dynamoEntity.getList().stream()
            .map(e -> (Article) e)
            .collect(Collectors.toList()),
        dynamoEntity.getNextPageToken()
    );
  }

  @Override
  @Log
  public Optional<Article> findById(@NonNull String tenant, @NonNull Article.Status status,
      @NonNull String id) {
    LOGGER.debug("find article by id [id = " + id + "]");
    Optional<ArticleObject> object = findItem(ArticleObject.of(
        new Article.Builder().tenant(tenant).status(status).id(id).build()
    ));
    if (object.isPresent()) {
      LOGGER.info("article found [id = " + object.get().getId() + "]");
    } else {
      LOGGER.info("article not found");
    }
    return object.map(e -> e);
  }

  @Override
  @Log
  public Optional<Article> findByName(String tenant, String name) {
    LOGGER.debug("find article by name [name = " + name + "]");
    ArticleNameRelation articleNameRelation = new ArticleNameRelation(tenant, name);
    Optional<ArticleNameRelation> relation = findItem(articleNameRelation);
    if (relation.isPresent()) {
      ArticleNameRelation r = relation.get();
      LOGGER.info("relation found [id = " + r.getId() + "]");
      return this.findById(tenant, r.getStatus(), r.getId());
    } else {
      LOGGER.info("relation not found");
      return Optional.empty();
    }
  }

  @Override
  @Log
  public PagingEntity<Article> findByTag(
      String tenant,
      Status status,
      String tagId,
      Integer limit,
      String nextPageToken
  ) {
    LOGGER.debug("find articles by tag [tag = " + tagId + "]");
    ArticleTagRelation articleTagRelation = new ArticleTagRelation(tenant, status, tagId);
    PagingEntity<ArticleTagRelation> tagEntities = findAllItems(articleTagRelation, limit,
        nextPageToken);

    List<ArticleObject> result = this.findManyItems(
        tagEntities.getList().stream().map(ArticleTagRelation::toArticle).toList());

    return new PagingEntity<>(
        result.stream().map(object -> (Article) object).toList(),
        tagEntities.getNextPageToken()
    );
  }

  @Override
  @Log
  public PagingEntity<Article> findByAuthor(
      String tenant,
      Status status,
      String authorId,
      Integer limit,
      String nextPageToken
  ) {
    LOGGER.debug("find articles by author [author = " + authorId + "]");
    ArticleAuthorRelation articleAuthorRelation = new ArticleAuthorRelation(
        tenant,
        status,
        authorId
    );
    PagingEntity<ArticleAuthorRelation> authorEntities = findAllItems(
        articleAuthorRelation,
        limit,
        nextPageToken
    );

    List<ArticleObject> result = this.findManyItems(
        authorEntities.getList().stream()
            .map(ArticleAuthorRelation::toArticle)
            .toList()
    );

    return new PagingEntity<>(
        result.stream().map(object -> (Article) object).toList(),
        authorEntities.getNextPageToken()
    );
  }

  @Override
  @Log
  public Article save(Article article) {
    Article old = null;
    if (article.getId() != null && !article.getId().isEmpty()) {
      var saved = findById(article.getTenant(), article.getStatus(), article.getId());
      if (saved.isPresent()) {
        old = saved.get();
      }
    }

    // Object
    ArticleObject object = ArticleObject.of(article);
    object.setId(idGenerator.generate());
    LOGGER.info("put article entity [ id = " + object.getId() + "]");
    putItem(object);

    // Relation - name
    if (old != null && !article.getName().equals(old.getName())) {
      deleteItem(new ArticleNameRelation(old));
    }
    putItem(new ArticleNameRelation(object));

    // Relation - tag
    if (old != null) {
      for (String oldTag : CollectionUtils.differenceSet(old.getTags(), article.getTags())) {
        deleteItem(
            new ArticleTagRelation(old.getTenant(), old.getStatus(), oldTag, old.getId()));
      }

      for (String newTag : CollectionUtils.differenceSet(article.getTags(), old.getTags())) {
        putItem(
            new ArticleTagRelation(object.getTenant(), object.getStatus(), newTag, object.getId()));
      }

    } else {
      for (ArticleTagRelation tagRelation : ArticleTagRelation.of(object)) {
        putItem(tagRelation);
      }
    }

    // Relation - author
    if (old != null && old.getAuthorId() != null && !old.getAuthorId()
        .equals(object.getAuthorId())) {
      deleteItem(new ArticleAuthorRelation(old));
    }
    putItem(new ArticleAuthorRelation(object));

    return object;
  }

  @Override
  public void delete(String tenant, String id) {
    Consumer<ArticleObject> deleteRelations = (article) -> {
      // ArticleName
      deleteItem(new ArticleNameRelation(article));

      // ArticleTag
      for (ArticleTagRelation tagRelation : ArticleTagRelation.of(article)) {
        deleteItem(tagRelation);
      }

      // ArticleAuthor
      deleteItem(new ArticleAuthorRelation(article));
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
  }
}
