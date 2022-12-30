package com.github.awesome_city.blog.api.util.id;

import io.micronaut.core.annotation.NonNull;

@FunctionalInterface
public interface IdGenerator {

  @NonNull
  String generate();
}
