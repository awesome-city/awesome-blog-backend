package com.github.awesome_city.blog.api.constant.error;

import io.micronaut.http.HttpStatus;

public class NoPermissionException extends MyException {

  public NoPermissionException(String message) {
    super(HttpStatus.FORBIDDEN, "E403", message);
  }
}
