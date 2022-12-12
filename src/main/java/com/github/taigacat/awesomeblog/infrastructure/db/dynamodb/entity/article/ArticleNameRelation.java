package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.article;

import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.DynamoDbConfiguration;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common.KeyAttribute;
import com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.entity.DynamoDbEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;

@DynamoDbBean
@EqualsAndHashCode
@ToString
public class ArticleNameRelation implements DynamoDbEntity<ArticleNameRelation> {

  @Setter
  @Getter
  private String id;

  @Setter
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

  @DynamoDbIgnore
  public String getName() {
    return name;
  }

  @Override
  public String getObjectName() {
    return "Article-name";
  }

  @Override
  public String getHashKey() {
    return this.createHashKeyValue(
        new KeyAttribute("name", this.getName())
    );
  }

  @Override
  public String getRangeKey() {
    return this.createRangeKeyValue();
  }

  @Override
  public DynamoDbTable<ArticleNameRelation> getTable(DynamoDbEnhancedClient enhancedClient,
      DynamoDbConfiguration dynamoDbConfiguration) {
    TableSchema<ArticleNameRelation> tableSchema = TableSchema.builder(
            ArticleNameRelation.class)
        .newItemSupplier(ArticleNameRelation::new)
        .addAttribute(String.class, attr -> attr.name(HASH_KEY)
            .getter(ArticleNameRelation::getHashKey)
            .setter(ArticleNameRelation::setHashKey)
            .tags(StaticAttributeTags.primaryPartitionKey()))
        .addAttribute(String.class, attr -> attr.name(RANGE_KEY)
            .getter(ArticleNameRelation::getRangeKey)
            .setter(ArticleNameRelation::setHashKey)
            .tags(StaticAttributeTags.primarySortKey()))
        .build();

    return enhancedClient.table(
        dynamoDbConfiguration.getRelationTableName(),
        tableSchema
    );
  }
}
