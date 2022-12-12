package com.github.taigacat.awesomeblog.infrastructure.db.dynamodb.common;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public final class KeyAttribute {

  private final String name;
  private final String value;

  public KeyAttribute(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String join() {
    return name + "=" + "\"" + value + "\";";
  }

}
