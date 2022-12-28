package com.github.taigacat.awesomeblog.constant.error;

import io.micronaut.http.HttpStatus;

public class ResourceConflictException extends MyException {

  public ResourceConflictException(String message) {
    super(HttpStatus.CONFLICT, "E409", message);
  }
}
