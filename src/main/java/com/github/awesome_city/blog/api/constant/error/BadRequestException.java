package com.github.awesome_city.blog.api.constant.error;

import io.micronaut.http.HttpStatus;

public class BadRequestException extends MyException {

  public BadRequestException(String message) {
    super(HttpStatus.BAD_REQUEST, "E400", message);
  }
}
