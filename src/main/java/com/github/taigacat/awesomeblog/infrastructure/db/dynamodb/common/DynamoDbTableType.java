package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common;

public enum DynamoDbTableType {
  OBJECT_TABLE {
    @Override
    public String getTableName(DynamoDbConfiguration config) {
      return config.getObjectTableName();
    }
  },
  RELATION_TABLE {
    @Override
    public String getTableName(DynamoDbConfiguration config) {
      return config.getRelationTableName();
    }
  };

  public abstract String getTableName(DynamoDbConfiguration config);
}
