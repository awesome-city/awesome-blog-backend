package com.github.awesome_city.blog.api.infrastructure.db.dynamodb.repository;

import com.github.awesome_city.blog.api.constant.error.ResourceConflictException;
import com.github.awesome_city.blog.api.constant.error.ResourceNotFoundException;
import com.github.awesome_city.blog.api.domain.common.PagingEntity;
import com.github.awesome_city.blog.api.domain.entity.Article;
import com.github.awesome_city.blog.api.domain.entity.Article.Status;
import com.github.awesome_city.blog.api.domain.repository.ArticleRepository;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.common.DynamoDbManager;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity.article.ArticleAuthorRelation;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity.article.ArticleIdRelation;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity.article.ArticleNameRelation;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity.article.ArticleObject;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity.article.ArticleTagRelation;
import com.github.awesome_city.blog.api.util.CollectionUtils;
import com.github.awesome_city.blog.api.util.aspect.Log;
import com.github.awesome_city.blog.api.util.id.IdGenerator;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DynamoDbArticleRepository implements ArticleRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbArticleRepository.class);

  private final DynamoDbManager manager;

  private final IdGenerator idGenerator;

  public DynamoDbArticleRepository(
      DynamoDbManager manager,
      IdGenerator idGenerator
  ) {
    this.manager = manager;
    this.idGenerator = idGenerator;
  }

  @Override
  @Log
  public PagingEntity<Article> findAll(String site, Article.Status status, Integer limit,
      String nextPageToken) {
    PagingEntity<ArticleObject> dynamoEntities = manager.findAllItems(
        ArticleObject.of(Article.builder().site(site).status(status).build()),
        limit,
        nextPageToken
    );

    return new PagingEntity<>(
        dynamoEntities.getList().stream()
            .map(e -> (Article) e)
            .collect(Collectors.toList()),
        dynamoEntities.getNextPageToken()
    );
  }

  @Override
  @Log
  public Optional<Article> findById(
      @NonNull String site,
      @NonNull String id
  ) {
    LOGGER.debug("find article by id [id = " + id + "]");
    Optional<ArticleIdRelation> idRelation = manager.findItem(new ArticleIdRelation(site, id));

    Optional<ArticleObject> object = idRelation
        .flatMap(r -> manager.findItem(ArticleObject.of(
            Article.builder()
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
    Optional<ArticleNameRelation> relation = manager.findItem(articleNameRelation);
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
    PagingEntity<ArticleTagRelation> tagEntities = manager.findAllItems(articleTagRelation, limit,
        nextPageToken);

    List<ArticleObject> result = manager.findManyItems(
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
    PagingEntity<ArticleAuthorRelation> authorEntities = manager.findAllItems(
        articleAuthorRelation,
        limit,
        nextPageToken
    );

    List<ArticleObject> result = manager.findManyItems(
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
    if (this.existName(article)) {
      throw new ResourceConflictException("article name is already used by another article.");
    }

    // Object
    ArticleObject object = ArticleObject.of(article);
    object.setId(idGenerator.generate());
    LOGGER.info("create article entity [ id = " + object.getId() + "]");
    manager.putItem(object);

    // Relation - id
    manager.putItem(new ArticleIdRelation(object));

    // Relation - name
    manager.putItem(new ArticleNameRelation(object));

    // Relation - tag
    for (ArticleTagRelation tagRelation : ArticleTagRelation.of(object)) {
      manager.putItem(tagRelation);
    }

    // Relation - author
    manager.putItem(new ArticleAuthorRelation(object));

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
    if (this.existName(article)) {
      throw new ResourceConflictException("article name is already used by another article.");
    }

    // Object
    ArticleObject object = ArticleObject.of(article);
    LOGGER.info("update article entity [ id = " + object.getId() + "]");
    manager.updateItem(object);

    // Relation - name
    // 名前が変わっていたら削除＆作成
    if (!article.getName().equals(old.getName())) {
      manager.deleteItem(new ArticleNameRelation(old));
      manager.putItem(new ArticleNameRelation(object));
    }

    // Relation - tag
    for (String oldTag : CollectionUtils.differenceSet(old.getTags(), article.getTags())) {
      manager.deleteItem(
          new ArticleTagRelation(old.getSite(), old.getStatus(), oldTag, old.getId()));
    }

    for (String newTag : CollectionUtils.differenceSet(article.getTags(), old.getTags())) {
      manager.putItem(
          new ArticleTagRelation(object.getSite(), object.getStatus(), newTag, object.getId()));
    }

    // Relation - author
    // Authorが変わっていたら削除＆作成
    if (old.getAuthorId() != null && !old.getAuthorId().equals(object.getAuthorId())) {
      manager.deleteItem(new ArticleAuthorRelation(old));
      manager.putItem(new ArticleAuthorRelation(object));
    }

    return object;
  }

  @Override
  public void delete(String site, String id) {
    UnaryOperator<ArticleObject> deleteRelations = (article) -> {
      // ArticleId
      manager.deleteItem(new ArticleIdRelation(article));

      // ArticleName
      manager.deleteItem(new ArticleNameRelation(article));

      // ArticleTag
      for (ArticleTagRelation tagRelation : ArticleTagRelation.of(article)) {
        manager.deleteItem(tagRelation);
      }

      // ArticleAuthor
      manager.deleteItem(new ArticleAuthorRelation(article));

      return article;
    };

    this.findById(site, id)
        .map(ArticleObject::of)
        .map(deleteRelations)
        .ifPresent(manager::deleteItem);
  }

  private boolean existName(Article article) {
    return manager.findItem(new ArticleNameRelation(article.getSite(), article.getName()))
        .map(r -> !(r.getSite().equals(article.getSite()) && r.getId().equals(article.getId())))
        .orElse(false);
  }
}
