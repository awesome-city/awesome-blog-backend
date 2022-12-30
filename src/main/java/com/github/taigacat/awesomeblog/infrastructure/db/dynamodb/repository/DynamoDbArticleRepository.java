package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.repository;

import com.github.taigacat.awesomeblog.constant.error.ResourceConflictException;
import com.github.taigacat.awesomeblog.constant.error.ResourceNotFoundException;
import com.github.taigacat.awesomeblog.domain.common.PagingEntity;
import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.domain.entity.Article.Status;
import com.github.taigacat.awesomeblog.domain.repository.ArticleRepository;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbConfiguration;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.article.ArticleAuthorRelation;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.article.ArticleIdRelation;
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
  public PagingEntity<Article> findAll(String site, Article.Status status, Integer limit,
      String nextPageToken) {
    PagingEntity<ArticleObject> dynamoEntity = findAllItems(
        ArticleObject.of(new Article.Builder().site(site).status(status).build()),
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
  public Optional<Article> findById(
      @NonNull String site,
      @NonNull String id
  ) {
    LOGGER.debug("find article by id [id = " + id + "]");
    Optional<ArticleIdRelation> idRelation = findItem(new ArticleIdRelation(site, id));

    Optional<ArticleObject> object = idRelation
        .flatMap(r -> findItem(ArticleObject.of(
            new Article.Builder()
                .site(r.getSite())
                .status(r.getStatus())
                .id(r.getId())
                .build()
        )));

    if (object.isPresent()) {
      LOGGER.info("article found [id = " + object.get().getId() + "]");
    } else {
      LOGGER.info("article not found");
    }
    return object.map(e -> e);
  }

  @Override
  @Log
  public Optional<Article> findByName(String site, String name) {
    LOGGER.debug("find article by name [name = " + name + "]");
    ArticleNameRelation articleNameRelation = new ArticleNameRelation(site, name);
    Optional<ArticleNameRelation> relation = findItem(articleNameRelation);
    if (relation.isPresent()) {
      ArticleNameRelation r = relation.get();
      LOGGER.info("relation found [id = " + r.getId() + "]");
      return this.findById(site, r.getId());
    } else {
      LOGGER.info("relation not found");
      return Optional.empty();
    }
  }

  @Override
  @Log
  public PagingEntity<Article> findByTag(
      String site,
      Status status,
      String tagId,
      Integer limit,
      String nextPageToken
  ) {
    LOGGER.debug("find articles by tag [tag = " + tagId + "]");
    ArticleTagRelation articleTagRelation = new ArticleTagRelation(site, status, tagId);
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
      String site,
      Status status,
      String authorId,
      Integer limit,
      String nextPageToken
  ) {
    LOGGER.debug("find articles by author [author = " + authorId + "]");
    ArticleAuthorRelation articleAuthorRelation = new ArticleAuthorRelation(
        site,
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
  public Article create(Article article) {
    // 同じnameで他の記事が存在している場合はエラー
    if (!this.isUniqueName(article)) {
      throw new ResourceConflictException("article name is already used by another article.");
    }

    // Object
    ArticleObject object = ArticleObject.of(article);
    object.setId(idGenerator.generate());
    LOGGER.info("create article entity [ id = " + object.getId() + "]");
    putItem(object);

    // Relation - id
    putItem(new ArticleIdRelation(object));

    // Relation - name
    putItem(new ArticleNameRelation(object));

    // Relation - tag
    for (ArticleTagRelation tagRelation : ArticleTagRelation.of(object)) {
      putItem(tagRelation);
    }

    // Relation - author
    putItem(new ArticleAuthorRelation(object));

    return object;
  }

  @Override
  @Log
  public Article update(Article article) {
    if (article.getId() == null) {
      throw new IllegalArgumentException();
    }

    // 保存済の記事が無い場合はエラー
    Article old = findById(article.getSite(), article.getId())
        .orElseThrow(
            () -> new ResourceNotFoundException("article not found")
        );
    assert old != null;

    // 同じnameで他の記事が存在している場合はエラー
    if (!this.isUniqueName(article)) {
      throw new ResourceConflictException("article name is already used by another article.");
    }

    // Object
    ArticleObject object = ArticleObject.of(article);
    LOGGER.info("update article entity [ id = " + object.getId() + "]");
    updateItem(object);

    // Relation - name
    // 名前が変わっていたら削除＆作成
    if (!article.getName().equals(old.getName())) {
      deleteItem(new ArticleNameRelation(old));
      putItem(new ArticleNameRelation(object));
    }

    // Relation - tag
    for (String oldTag : CollectionUtils.differenceSet(old.getTags(), article.getTags())) {
      deleteItem(
          new ArticleTagRelation(old.getSite(), old.getStatus(), oldTag, old.getId()));
    }

    for (String newTag : CollectionUtils.differenceSet(article.getTags(), old.getTags())) {
      putItem(
          new ArticleTagRelation(object.getSite(), object.getStatus(), newTag, object.getId()));
    }

    // Relation - author
    // Authorが変わっていたら削除＆作成
    if (old.getAuthorId() != null && !old.getAuthorId().equals(object.getAuthorId())) {
      deleteItem(new ArticleAuthorRelation(old));
      putItem(new ArticleAuthorRelation(object));
    }

    return object;
  }

  @Override
  public void delete(String site, String id) {
    Consumer<ArticleObject> deleteRelations = (article) -> {
      // ArticleId
      deleteItem(new ArticleIdRelation(article));

      // ArticleName
      deleteItem(new ArticleNameRelation(article));

      // ArticleTag
      for (ArticleTagRelation tagRelation : ArticleTagRelation.of(article)) {
        deleteItem(tagRelation);
      }

      // ArticleAuthor
      deleteItem(new ArticleAuthorRelation(article));
    };

    this.findById(site, id)
        .ifPresent(
            article -> deleteItem(ArticleObject.of(
                new Article.Builder().site(site).status(Status.PUBLISHED).id(id).build()
            ))
        );
  }

  private boolean isUniqueName(Article article) {
    return findItem(new ArticleNameRelation(article.getSite(), article.getName()))
        .map(r -> !r.getId().equals(article.getId()))
        .orElse(true);
  }
}
