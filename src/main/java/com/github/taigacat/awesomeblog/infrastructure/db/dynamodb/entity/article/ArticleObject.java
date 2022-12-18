package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.article;

import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbSupport;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbTableType;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.DynamoDbEntity;
import com.github.taigacat.awesomeblog.util.micronaut.BeanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class ArticleObject extends Article implements DynamoDbEntity {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArticleObject.class);

  @NonNull
  public static ArticleObject of(Article article) {
    return BeanUtil.createAndCopy(article, ArticleObject.class);
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
        "tenant", this.getTenant(),
        "status", this.getStatus().name().toLowerCase()
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

//  @Override
//  public Status getStatus() {
//    return super.getStatus();
//  }
//
//  @Override
//  public void setStatus(Status status) {
//    super.setStatus(status);
//  }

//  public String getStatusString() {
//    return this.getStatus().name().toLowerCase();
//  }
//
//  public void setStatusString(String status) {
//    this.setStatus(Article.Status.valueOf(status.toUpperCase()));
//  }

}
