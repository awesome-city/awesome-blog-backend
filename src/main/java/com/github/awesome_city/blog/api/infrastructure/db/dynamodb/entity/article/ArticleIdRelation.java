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
public class ArticleIdRelation implements DynamoDbEntity {

  private String site;
  private String id;

  private Article.Status status;

  public ArticleIdRelation(String site, String id) {
    this.site = site;
    this.id = id;
  }

  public ArticleIdRelation(Article object) {
    this.site = object.getSite();
    this.id = object.getId();
    this.status = object.getStatus();
  }

  public ArticleObject toArticle() {
    ArticleObject object = new ArticleObject();
    object.setSite(site);
    object.setStatus(status);
    object.setId(id);
    return object;
  }

  @Override
  public DynamoDbTableType getTableType() {
    return DynamoDbTableType.RELATION_TABLE;
  }

  @Override
  @DynamoDbPartitionKey
  public String getHashKey() {
    return DynamoDbSupport.createHashKeyValue(
        "Article-id",
        "site", this.getSite()
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
