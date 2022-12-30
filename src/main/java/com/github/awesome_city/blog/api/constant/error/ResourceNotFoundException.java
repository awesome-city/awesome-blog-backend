package com.github.awesome_city.blog.api.constant.error;

import io.micronaut.http.HttpStatus;

public class ResourceNotFoundException extends MyException {

  public ResourceNotFoundException(String message) {
    super(HttpStatus.NOT_FOUND, "E404", message);
  }
}
