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
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@EqualsAndHashCode
@ToString
@Data
@NoArgsConstructor
public class ArticleAuthorRelation implements DynamoDbEntity {

  private String tenant;
  private String id;

  private String authorId;

  public ArticleAuthorRelation(String tenant, String authorId) {
    this.tenant = tenant;
    this.authorId = authorId;
  }

  public ArticleAuthorRelation(String tenant, String authorId, String id) {
    this.tenant = tenant;
    this.authorId = authorId;
    this.id = id;
  }

  public ArticleAuthorRelation(Article object) {
    this.tenant = object.getTenant();
    this.authorId = object.getAuthorId();
    this.id = object.getId();
  }

  @Override
  public DynamoDbTableType getTableType() {
    return DynamoDbTableType.RELATION_TABLE;
  }

  @Override
  @DynamoDbPartitionKey
  public String getHashKey() {
    return DynamoDbSupport.createHashKeyValue(
        "Article-tag",
        "tenant", this.getTenant(),
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
