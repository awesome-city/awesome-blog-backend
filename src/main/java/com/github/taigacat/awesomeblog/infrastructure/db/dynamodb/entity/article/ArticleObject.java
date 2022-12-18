package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.article;

import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbSupport;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbTableType;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.DynamoDbEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class ArticleObject extends Article implements DynamoDbEntity {

  public ArticleObject(Article.Status status) {
    super(status);
  }

  public ArticleObject(Article.Status status, String id) {
    super(status, id);
  }

  public static ArticleObject fromArticle(Article article) {
    ArticleObject object = new ArticleObject();
    object.setStatus(article.getStatus());
    return object;
  }

  @Override
  public DynamoDbTableType getTableType() {
    return DynamoDbTableType.OBJECT_TABLE;
  }

  @Override
  @DynamoDbPartitionKey
  public String getHashKey() {
    return DynamoDbSupport.createHashKeyValue(
        "Article",
        "status", getStatus().name().toLowerCase()
    );
  }

  @Override
  @DynamoDbSortKey
  public String getRangeKey() {
    return DynamoDbSupport.createRangeKeyValue(
        "id", getId()
    );
  }

  @Override
  public void setHashKey(String hashKey) {
  }

  @Override
  public void setRangeKey(String rangeKey) {
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

}
