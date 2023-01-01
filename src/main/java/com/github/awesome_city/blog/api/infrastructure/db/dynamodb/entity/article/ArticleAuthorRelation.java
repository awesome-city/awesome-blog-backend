package com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity.article;

import com.github.awesome_city.blog.api.domain.entity.Article;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.common.DynamoDbSupport;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.common.DynamoDbTableType;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity.DynamoDbEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@EqualsAndHashCode
@ToString
@Data
@NoArgsConstructor
public class ArticleAuthorRelation implements DynamoDbEntity {

  private String site;
  private String id;

  private String authorId;

  private Article.Status status;

  public ArticleAuthorRelation(String site, Article.Status status, String authorId) {
    this.site = site;
    this.status = status;
    this.authorId = authorId;
  }

  public ArticleAuthorRelation(String site, Article.Status status, String authorId, String id) {
    this.site = site;
    this.status = status;
    this.authorId = authorId;
    this.id = id;
  }

  public ArticleAuthorRelation(Article object) {
    this.site = object.getSite();
    this.status = object.getStatus();
    this.authorId = object.getAuthorId();
    this.id = object.getId();
  }

  public ArticleObject toArticle() {
    ArticleObject object = new ArticleObject();
    object.setSite(site);
    object.setStatus(status);
    object.setId(id);
    return object;
  }

  @Override
  public DynamoDbTableType tableType() {
    return DynamoDbTableType.RELATION_TABLE;
  }

  @Override
  @DynamoDbPartitionKey
  public String getHashKey() {
    return DynamoDbSupport.createHashKeyValue(
        "Article-tag",
        "site", this.getSite(),
        "authorId", this.getAuthorId()
    );
  }

  @Override
  @DynamoDbSortKey
  public String getRangeKey() {
    return DynamoDbSupport.createRangeKeyValue(
        "id", this.getId()
    );
  }

  @Override
  public void setHashKey(String hashKey) {

  }

  @Override
  public void setRangeKey(String rangeKey) {

  }

}
