package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.article;

import com.github.taigacat.awesomeblog.domain.entity.Article;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbSupport;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbTableType;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.DynamoDbEntity;
import io.micronaut.core.annotation.NonNull;
import java.util.HashSet;
import java.util.Set;
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
public class ArticleTagRelation implements DynamoDbEntity {

  private String tenant;
  private String id;

  private String tagId;

  public ArticleTagRelation(String tenant, String tagId) {
    this.tenant = tenant;
    this.tagId = tagId;
  }

  public ArticleTagRelation(String tenant, String tagId, String id) {
    this.tenant = tenant;
    this.tagId = tagId;
    this.id = id;
  }

  @NonNull
  public static Set<ArticleTagRelation> of(Article object) {
    Set<ArticleTagRelation> tagRelations = new HashSet<>();
    if (object.getTags() != null) {
      for (String tag : object.getTags()) {
        ArticleTagRelation relation = new ArticleTagRelation(object.getTenant(), tag,
            object.getId());
        tagRelations.add(relation);
      }
    }

    return tagRelations;
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
        "tagId", this.getTagId()
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
