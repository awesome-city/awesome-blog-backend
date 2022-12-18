package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.article;

import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbSupport;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbTableType;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.DynamoDbEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@EqualsAndHashCode
@ToString
@Data
public class ArticleNameRelation implements DynamoDbEntity {

  private String id;

  private String name;

  public ArticleNameRelation() {
  }


  public static ArticleNameRelation ofName(String name) {
    ArticleNameRelation relation = new ArticleNameRelation();
    relation.name = name;
    return relation;
  }

  public static ArticleNameRelation ofIdName(String id, String name) {
    ArticleNameRelation relation = new ArticleNameRelation();
    relation.id = id;
    relation.name = name;
    return relation;
  }

  @Override
  public DynamoDbTableType getTableType() {
    return DynamoDbTableType.RELATION_TABLE;
  }

  @Override
  @DynamoDbPartitionKey
  public String getHashKey() {
    return DynamoDbSupport.createHashKeyValue(
        "Article-name",
        "name", this.getName()
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
