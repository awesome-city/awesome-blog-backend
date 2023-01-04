package com.github.awesome_city.blog.api.infrastructure.db.dynamodb.entity.site;

import com.github.awesome_city.blog.api.domain.entity.Site;
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
public class SiteDomainRelation implements DynamoDbEntity {

  private String id;
  private String domain;

  public SiteDomainRelation(String domain) {
    this.domain = domain;
  }

  public SiteDomainRelation(Site object) {
    this.id = object.getId();
    this.domain = object.getDomain();
  }


  @Override
  public DynamoDbTableType tableType() {
    return DynamoDbTableType.RELATION_TABLE;
  }

  @Override
  @DynamoDbPartitionKey
  public String getHashKey() {
    return DynamoDbSupport.createHashKeyValue("Site-domain");
  }

  @Override
  @DynamoDbSortKey
  public String getRangeKey() {
    return DynamoDbSupport.createRangeKeyValue("domain", getDomain());
  }

  @Override
  public void setHashKey(String hashKey) {

  }

  @Override
  public void setRangeKey(String rangeKey) {

  }
}
