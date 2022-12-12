package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.article;

import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbConfiguration;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.KeyAttribute;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.DynamoDbEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class ArticleObject extends Article implements
    DynamoDbEntity<ArticleObject> {

  public ArticleObject() {
    super();
  }

  public ArticleObject(String id) {
    super();
    this.setId(id);
  }

  public static ArticleObject fromArticle(Article article) {
    ArticleObject object = new ArticleObject();
    object.setStatus(article.getStatus());
    return object;
  }

  @Override
  public String getObjectName() {
    return "Article";
  }

  @Override
  @DynamoDbPartitionKey
  public String getHashKey() {
    return this.createHashKeyValue(
        new KeyAttribute("status", this.getStatus().name().toLowerCase())
    );
  }

  @Override
  @DynamoDbSortKey
  public String getRangeKey() {
    return this.createRangeKeyValue(
        new KeyAttribute("id", getId())
    );
  }

  @Override
  @DynamoDbIgnore
  public Status getStatus() {
    return super.getStatus();
  }

  @Override
  @DynamoDbIgnore
  public void setStatus(Status status) {
    super.setStatus(status);
  }

  public String getStatusString() {
    return this.getStatus().name().toLowerCase();
  }

  public void setStatusString(String status) {
    this.setStatus(Article.Status.valueOf(status.toUpperCase()));
  }

  @Override
  public DynamoDbTable<ArticleObject> getTable(
      DynamoDbEnhancedClient enhancedClient,
      DynamoDbConfiguration dynamoDbConfiguration
  ) {
    TableSchema<ArticleObject> tableSchema = TableSchema.builder(
            ArticleObject.class)
        .newItemSupplier(ArticleObject::new)
        .addAttribute(String.class, attr -> attr.name(HASH_KEY)
            .getter(ArticleObject::getHashKey)
            .setter(ArticleObject::setHashKey)
            .tags(StaticAttributeTags.primaryPartitionKey()))
        .addAttribute(String.class, attr -> attr.name(RANGE_KEY)
            .getter(ArticleObject::getRangeKey)
            .setter(ArticleObject::setHashKey)
            .tags(StaticAttributeTags.primarySortKey()))
        .build();

    return enhancedClient.table(
        dynamoDbConfiguration.getObjectTableName(),
        tableSchema
    );
  }
}
