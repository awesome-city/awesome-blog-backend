package com.github.taigacat.awesomeblog.domain.common;

import io.micronaut.core.annotation.NonNull;

public interface Identified {

  @NonNull
  String getId();
}
