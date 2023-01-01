package com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity.article;

import com.github.awesome_city.blog.api.domain.entity.Article;
import com.github.awesome_city.blog.api.domain.entity.Article.Status;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.common.DynamoDbSupport;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.common.DynamoDbTableType;
import com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity.DynamoDbEntity;
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

  private String site;
  private String id;

  private Status status;

  private String tagId;

  public ArticleTagRelation(String site, Status status, String tagId) {
    this.site = site;
    this.status = status;
    this.tagId = tagId;
  }

  public ArticleTagRelation(String site, Status status, String tagId, String id) {
    this.site = site;
    this.status = status;
    this.tagId = tagId;
    this.id = id;
  }

  @NonNull
  public static Set<ArticleTagRelation> of(Article object) {
    Set<ArticleTagRelation> tagRelations = new HashSet<>();
    if (object.getTags() != null) {
      for (String tag : object.getTags()) {
        ArticleTagRelation relation = new ArticleTagRelation(
            object.getSite(),
            object.getStatus(),
            tag,
            object.getId()
        );
        tagRelations.add(relation);
      }
    }

    return tagRelations;
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
