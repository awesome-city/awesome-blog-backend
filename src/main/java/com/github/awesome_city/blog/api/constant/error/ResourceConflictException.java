package com.github.awesome_city.blog.api.constant.error;

import io.micronaut.http.HttpStatus;

public class ResourceConflictException extends MyException {

  public ResourceConflictException(String message) {
    super(HttpStatus.CONFLICT, "E409", message);
  }
}
